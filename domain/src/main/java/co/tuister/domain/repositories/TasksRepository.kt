package co.tuister.domain.repositories

import co.tuister.domain.entities.Task

interface TasksRepository {
    suspend fun save(task: Task): Task
    suspend fun getTasks(): List<Task>
    suspend fun remove(item: Task): Boolean
}
