package co.tuister.domain.usecases.tasks

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Right
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Task
import co.tuister.domain.repositories.TasksRepository

class SaveTaskUseCase(
    private val repository: TasksRepository
) : UseCase<Task, Task>() {
    override suspend fun run(params: Task): Either<Failure, Task> {
        return when (val result = repository.save(params)) {
            is Either.Left -> result
            is Right -> {
                Right(result.value)
            }
        }
    }


}