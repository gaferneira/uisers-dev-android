package co.tuister.domain.repositories

import co.tuister.domain.entities.Task

interface TasksRepository {
    suspend fun getTasks(): List<Task>
    suspend fun save(task: Task): Task
}
