package co.tuister.domain.usecases.tasks

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Task
import co.tuister.domain.repositories.TasksRepository

class GetMyTasksUseCase(
    private val repository: TasksRepository
) : NoParamsUseCase<List<Task>> {
    override suspend fun invoke(): Either<Failure, List<Task>> {
        return try {
            repository.getTasks()
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
