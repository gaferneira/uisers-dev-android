package co.tuister.data.migration

import co.tuister.data.dto.DataTasksUserDto
import co.tuister.data.dto.NoteDto
import co.tuister.data.dto.SchedulePeriodDto
import co.tuister.data.dto.SemesterUserDto
import co.tuister.data.dto.SubjectUserDto
import co.tuister.data.dto.TaskDto
import co.tuister.data.repositories.MyCareerRepository
import co.tuister.data.utils.BackupCollection
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.data.utils.SemestersCollection
import co.tuister.data.utils.TaskManagerCollection
import co.tuister.data.utils.UsersCollection
import co.tuister.data.utils.await
import co.tuister.data.utils.getEmail
import co.tuister.data.utils.objectToMap
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.repositories.MigrationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.util.Date

class MigrationRepositoryImpl(
    private val gson: Gson,
    val db: FirebaseFirestore,
    val auth: FirebaseAuth,
    connectivityUtil: ConnectivityUtil
) : MyCareerRepository(auth, db, connectivityUtil), MigrationRepository {

    private val taskManagerCollection by lazy { TaskManagerCollection(db) }
    private val backupCollection by lazy { BackupCollection(db, connectivityUtil) }
    private val usersCollection by lazy { UsersCollection(db, connectivityUtil) }

    override
    suspend fun migrate(): Either<Failure, Boolean> {
        try {

            val email = firebaseAuth.getEmail()
            val migration = usersCollection.getByEmail(email)
                ?.getBoolean(UsersCollection.FIELD_USER_MIGRATION) ?: false

            val text = when {
                migration -> null
                else -> backupCollection.getUserBackup(email)
            }

            if (text.isNullOrEmpty()) {
                return Either.Right(true)
            }

            val migrationData = gson.fromJson(text, MigrationData::class.java)

            if (migrationData.materiaEstudianteList.isEmpty() &&
                migrationData.tareaList.isEmpty()
            ) {
                finishProcessMigration(email)
                return Either.Right(false)
            }

            val semestres = migrationData.semestreEstudianteList.map {
                SemesterUserDto(
                    it.ponderado.toString(),
                    "" + it.fkSemestre!!.toString(),
                    it.creditos.toString()
                )
            }

            val materias = migrationData.materiaEstudianteList.groupBy {
                it.fkSemestreEstudiante!!.fkSemestre!!.toString()
            }.map {
                Pair(
                    it.component1(),
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
                (
                    it.fkMateriaEstudiante!!.fkSemestreEstudiante!!.fkSemestre!!.toString() + DELIMITER +
                        it.fkMateriaEstudiante!!.fkMateriaCarrera!!.fkMateria!!.idMateria
                    )
            }.map {
                Pair(
                    it.component1(),
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
                Pair(
                    it.component1(),
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
                    val index: String = it.period + DELIMITER + sub.code
                    notas[index]?.forEach { note ->
                        addNote(docMateria, note)
                    }
                }

                bloques[it.period]?.forEach { per ->
                    addPeriod(doc, per)
                }
            }

            finishProcessMigration(email)

            return Either.Right(true)
        } catch (e: Exception) {
            return Either.Left(Failure.analyzeException(e))
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
        val email = firebaseAuth.getEmail()
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

    companion object {
        private const val DELIMITER = "/////"
    }
}
