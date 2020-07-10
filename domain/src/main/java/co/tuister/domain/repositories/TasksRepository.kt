package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Task

interface TasksRepository {
    suspend fun save(task: Task): Either<Failure, Task>
    suspend fun getTasks(): Either<Failure, List<Task>>
    suspend fun remove(item: Task): Either<Failure, Boolean>
}
