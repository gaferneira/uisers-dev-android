package co.tuister.domain.usecases.tasks

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Task
import co.tuister.domain.repositories.TasksRepository

class GetMyTasksUseCase(
    private val repository: TasksRepository
) : NoParamsUseCase<List<Task>>() {
    override suspend fun run(): Either<Failure, List<Task>> {
        return repository.getTasks()
    }
}