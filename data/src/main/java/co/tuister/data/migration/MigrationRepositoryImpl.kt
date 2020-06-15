package co.tuister.data.migration

import android.content.Context
import co.tuister.data.await
import co.tuister.data.dto.*
import co.tuister.data.repositories.MyCareerRepository
import co.tuister.data.utils.*
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.repositories.MigrationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.*

class MigrationRepositoryImpl(
    private val context: Context,
    private val gson: Gson,
    val db: FirebaseFirestore,
    val auth: FirebaseAuth
) : MyCareerRepository(auth, db), MigrationRepository {

    private val taskManagerCollection by lazy { TaskManagerCollection(db) }
    private val backupCollection by lazy { BackupCollection(db) }
    private val usersCollection by lazy { UsersCollection(db) }

    override
    suspend fun migrate(): Either<Failure, Boolean> {
        try {

            val email = firebaseAuth.currentUser!!.email!!
            val migration = usersCollection.getByEmail(email)
                ?.getBoolean(UsersCollection.FIELD_USER_MIGRATION) ?: false

            if (migration) {
                return Either.Right(true)
            }

            val text = backupCollection.getUserBackup(email) ?: return Either.Right(true)

            val migrationData = gson.fromJson(text, MigrationData::class.java)

            if (migrationData.materiaEstudianteList.isEmpty() &&
                migrationData.tareaList.isEmpty()
            ) {
                finishProcessMigration(email)
                return Either.Right(false)
            }

            val semestres = migrationData.semestreEstudianteList.map {
                SemesterUserDto(
                    it.promedioSemestral.toString(),
                    "" + it.fkSemestre!!.toString(),
                    it.creditos.toString()
                )
            }

            val materias = migrationData.materiaEstudianteList.groupBy {
                it.fkSemestreEstudiante!!.fkSemestre!!.toString()
            }.map {
                Pair(it.component1(),
                    it.component2().map { sub ->
                        SubjectUserDto(
                            sub.fkMateriaCarrera!!.fkMateria!!.idMateria.toString(),
                            sub.fkMateriaCarrera!!.fkMateria!!.nombreMateria.toString(),
                            "",
                            sub.definitiva,
                            sub.fkMateriaCarrera!!.fkMateria!!.creditos
                        )
                    }
                )
            }.toMap()

            val notas = migrationData.notaList.groupBy {
                it.fkMateriaEstudiante!!.fkSemestreEstudiante!!.fkSemestre!!.toString() + "///" + it.fkMateriaEstudiante!!.fkMateriaCarrera!!.fkMateria!!.idMateria
            }.map {
                Pair(it.component1(),
                    it.component2().map { item ->
                        NoteDto(
                            item.descripcion!!,
                            item.valor,
                            item.porcentaje.toFloat(),
                            item.total
                        )
                    }
                )
            }.toMap()

            val bloques = migrationData.bloqueMateriaList.groupBy {
                it.fkMateriaEstudiante!!.fkSemestreEstudiante!!.fkSemestre!!.toString()
            }.map {
                Pair(it.component1(),
                    it.component2().map { item ->
                        SchedulePeriodDto(
                            item.fkMateriaEstudiante!!.fkMateriaCarrera!!.fkMateria!!.nombreMateria!!,
                            item.fkDia!!.idDia + 2,
                            item.horaInicio!!,
                            item.horaFin!!,
                            item.lugar!!
                        )
                    }
                )
            }.toMap()

            val time = Date().time
            val tareas = migrationData.tareaList.map {
                TaskDto(
                    it.titulo!!,
                    it.descripcion!!,
                    time
                )
            }

            val taskCollection = getTaskDocument().collection(TaskManagerCollection.COL_TASKS)
            tareas.forEach {
                addTask(taskCollection, it)
            }

            val lastPeriod = migrationData.semestreEstudianteList
                .maxBy { it.fkSemestre!!.toString() }!!
                .fkSemestre.toString()

            val semesterCol = getSemestersCollection()

            getUserDocument()
                .update(SemestersCollection.FIELD_CURRENT_SEMESTER, lastPeriod)
                .await()

            semestres.forEach {
                val doc: DocumentReference = addSemester(semesterCol, it)
                materias[it.period]?.forEach { sub ->
                    val docMateria = addSubject(doc, sub)
                    notas[it.period + "///" + sub.code]?.forEach { note ->
                        addNote(docMateria, note)
                    }
                }

                bloques[it.period]?.forEach { per ->
                    addPeriod(doc, per)
                }
            }

            finishProcessMigration(email)

            return Either.Right(true)
        } catch (exception: Exception) {
            exception.printStackTrace()
            return Either.Left(Failure.ServerError(exception))
        }
    }

    private suspend fun addTask(taskCollection: CollectionReference, task: TaskDto) {

        val oldTask = taskCollection
            .whereEqualTo("title", task.title)
            .whereEqualTo("description", task.description)
            .get().await()?.documents?.firstOrNull()

        if (oldTask == null) {
            taskCollection.add(task.objectToMap()).await()
        }
    }

    private suspend fun addSemester(
        semesterCol: CollectionReference,
        item: SemesterUserDto
    ): DocumentReference {

        val oldSemester = semesterCol.whereEqualTo("period", item.period)
            .get().await()?.documents?.firstOrNull()

        return oldSemester?.reference ?: semesterCol.add(
            item.objectToMap()
        ).await()!!
    }

    private suspend fun addSubject(
        semesterDoc: DocumentReference,
        item: SubjectUserDto
    ): DocumentReference {

        val oldSubject = semesterDoc.collection(SemestersCollection.COL_SUBJECTS).whereEqualTo(
            "code", item.code
        ).get().await()?.documents?.firstOrNull()


        return oldSubject?.reference ?: semesterDoc.collection(SemestersCollection.COL_SUBJECTS)
            .add(
                item.objectToMap()
            ).await()!!
    }

    private suspend fun addNote(docMateria: DocumentReference, note: NoteDto): DocumentReference? {

        val oldNote = docMateria.collection(SemestersCollection.COL_NOTES)
            .whereEqualTo("description", note.description)
            .whereEqualTo("score", note.score)
            .get().await()?.documents?.firstOrNull()

        return oldNote?.reference ?: docMateria.collection(SemestersCollection.COL_NOTES).add(
            note.objectToMap()
        ).await()
    }

    private suspend fun addPeriod(doc: DocumentReference, per: SchedulePeriodDto) {

        val oldPeriod = doc.collection(SemestersCollection.COL_SCHEDULE)
            .whereEqualTo("description", per.description)
            .whereEqualTo("initialHour", per.initialHour)
            .get().await()?.documents?.firstOrNull()

        if (oldPeriod?.reference == null) {
            doc.collection(SemestersCollection.COL_SCHEDULE).add(
                per.objectToMap()
            ).await()
        }
    }

    private suspend fun finishProcessMigration(email: String) {
        usersCollection.getByEmail(email)?.reference?.update(
            UsersCollection.FIELD_USER_MIGRATION, true
        )?.await()

        backupCollection.deleteUserBackup(email)
    }


    private suspend fun getTaskDocument() = taskManagerCollection.document(getTaskDocumentsId())

    private suspend fun getTaskDocumentsId(): String {
        val email = firebaseAuth.currentUser!!.email!!
        val id = taskManagerCollection.collection()
            .whereEqualTo(TaskManagerCollection.FIELD_EMAIL, email)
            .get()
            .await()!!
            .documents.firstOrNull()?.id

        return id ?: createInitialTaskDocument(email)
    }

    private suspend fun createInitialTaskDocument(email: String): String {
        return try {
            val data = DataTasksUserDto(email)
            taskManagerCollection.collection().add(data).await()!!.id
        } catch (exception: Exception) {
            exception.printStackTrace()
            ""
        }
    }
}