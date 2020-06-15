package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Task
import java.util.*

interface TasksRepository {
    suspend fun getTasks(): Either<Failure, List<Task>>
    suspend fun save(task: Task): Either<Failure, Task>
}