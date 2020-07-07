package co.tuister.data.repositories

import co.tuister.data.dto.DataTasksUserDto
import co.tuister.data.dto.TaskDto
import co.tuister.data.dto.toDTO
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.data.utils.TaskManagerCollection
import co.tuister.data.utils.await
import co.tuister.data.utils.getEmail
import co.tuister.data.utils.getSource
import co.tuister.data.utils.objectToMap
import co.tuister.domain.entities.Task
import co.tuister.domain.repositories.TasksRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class TasksRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val connectivityUtil: ConnectivityUtil
) : TasksRepository {

    private val taskManagerCollection by lazy { TaskManagerCollection(db) }

    override suspend fun getTasks(): List<Task> {
        val collection = getUserDocument().collection(TaskManagerCollection.COL_TASKS)
            .get(connectivityUtil.getSource())
            .await()!!

        val list = collection.documents.map {
            val path = it.reference.path
            it.toObject(TaskDto::class.java)!!.toEntity(path)
        }

        return list.sortedBy { it.title }
    }

    override suspend fun save(task: Task): Task {
        if (task.id.isNotEmpty()) {
            val subjectDto = taskManagerCollection.documentByPath(task.id).get(connectivityUtil.getSource()).await()!!
            subjectDto.reference.update(task.toDTO().objectToMap())
        } else {
            val id = getUserDocument()
                .collection(TaskManagerCollection.COL_TASKS)
                .add(task.toDTO())
                .await()!!.path
            task.id = id
        }
        return task
    }

    override suspend fun remove(item: Task): Boolean {
        return if (item.id.isNotEmpty()) {
            taskManagerCollection.documentByPath(item.id).delete().await()
            true
        } else {
            false
        }
    }

    private suspend fun getUserDocument() = taskManagerCollection.document(getUserDocumentsId())

    private suspend fun getUserDocumentsId(): String {
        val email = firebaseAuth.getEmail()
        val id = taskManagerCollection.collection()
            .whereEqualTo(TaskManagerCollection.FIELD_EMAIL, email)
            .get(connectivityUtil.getSource())
            .await()!!
            .documents.firstOrNull()?.id

        return id ?: createInitialDocument(email)
    }

    private suspend fun createInitialDocument(email: String): String {
        return try {
            val data = DataTasksUserDto(email)
            taskManagerCollection.collection().add(data).await()!!.id
        } catch (exception: Exception) {
            Timber.e(exception)
            ""
        }
    }
}
