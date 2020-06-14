package co.tuister.data.migration

import android.content.Context
import android.util.Log
import co.tuister.data.await
import co.tuister.data.dto.*
import co.tuister.data.repositories.MyCareerRepository
import co.tuister.data.utils.BaseCollection
import co.tuister.data.utils.SemestersCollection
import co.tuister.data.utils.TaskManagerCollection
import co.tuister.data.utils.objectToMap
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.repositories.MigrationRepository
import com.google.firebase.auth.FirebaseAuth
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

    override
    suspend fun migrate(): Either<Failure, Boolean> {
        try {
            context.assets.open("backup.json").bufferedReader().use { assets ->
                val text = assets.readText()
                val migrationData = gson.fromJson(text, MigrationData::class.java)

                if (migrationData.materiaEstudianteList.isEmpty() &&
                    migrationData.tareaList.isEmpty()
                ) {
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
                    taskCollection.add(it.objectToMap()).await()
                }

                val semesterCol = getSemestersCollection()

                val lastPeriod = migrationData.semestreEstudianteList.maxBy { it.fkSemestre!!.toString() }!!.fkSemestre.toString()
                getUserDocument()
                    .update(SemestersCollection.FIELD_CURRENT_SEMESTER, lastPeriod)
                    .await()

                semestres.forEach {
                    val doc = semesterCol.add(it.objectToMap()).await()!!
                    materias[it.period]?.forEach {sub ->
                        val docMateria = doc.collection(SemestersCollection.COL_SUBJECTS).add(
                            sub.objectToMap()
                        ).await()!!

                        notas[it.period + "///" + sub.code]?.forEach { note ->
                            docMateria.collection(SemestersCollection.COL_NOTES).add(
                                note.objectToMap()
                            ).await()
                        }
                    }

                    bloques[it.period]?.forEach { per ->
                        doc.collection(SemestersCollection.COL_SCHEDULE).add(
                            per.objectToMap()
                        ).await()
                    }
                }
            }
            return Either.Right(true)
        } catch (exception: Exception) {
            exception.printStackTrace()
            return Either.Left(Failure.ServerError(exception))
        }
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

    private suspend fun createInitialTaskDocument(email : String): String {
        return try {
            val data = DataTasksUserDto(email)
            taskManagerCollection.collection().add(data).await()!!.id
        } catch (exception: Exception) {
            exception.printStackTrace()
            ""
        }
    }
}