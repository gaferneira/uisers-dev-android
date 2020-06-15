package co.tuister.data.repositories

import co.tuister.data.await
import co.tuister.data.dto.DataTasksUserDto
import co.tuister.data.dto.TaskDto
import co.tuister.data.dto.toDTO
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.TaskManagerCollection
import co.tuister.data.utils.objectToMap
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Task
import co.tuister.domain.repositories.TasksRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TasksRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : TasksRepository {

    private val taskManagerCollection by lazy { TaskManagerCollection(db) }

    override suspend fun getTasks(): Either<Failure, List<Task>> {
        return try {
            val collection = getUserDocument().collection(TaskManagerCollection.COL_TASKS)
                .get()
                .await()!!

            val list = collection.documents.map {
                val path = it.reference.path
                it.toObject(TaskDto::class.java)!!.toEntity().apply {
                    id = path
                }
            }

            Either.Right(list.sortedBy { it.status })
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    override suspend fun save(task: Task): Either<Failure, Task> {
        return try {
            if (task.id.isNotEmpty()){
                val subjectDto = taskManagerCollection.documentByPath(task.id).get().await()!!
                    subjectDto.reference.update(task.toDTO().objectToMap())
            } else {
                val id = getUserDocument()
                    .collection(TaskManagerCollection.COL_TASKS)
                    .add(task.toDTO())
                    .await()!!.path
                task.id = id
            }
            Either.Right(task)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    private suspend fun getUserDocument() = taskManagerCollection.document(getUserDocumentsId())

    private suspend fun getUserDocumentsId(): String {
        val email = firebaseAuth.currentUser!!.email!!
        val id = taskManagerCollection.collection()
            .whereEqualTo(TaskManagerCollection.FIELD_EMAIL, email)
            .get()
            .await()!!
            .documents.firstOrNull()?.id

        return id ?: createInitialDocument(email)
    }

    private suspend fun createInitialDocument(email : String): String {
        return try {
            val data = DataTasksUserDto(email)
            taskManagerCollection.collection().add(data).await()!!.id
        } catch (exception: Exception) {
            exception.printStackTrace()
            ""
        }
    }

}