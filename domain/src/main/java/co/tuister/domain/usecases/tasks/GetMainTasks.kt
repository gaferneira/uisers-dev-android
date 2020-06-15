package co.tuister.domain.usecases.tasks

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Task
import co.tuister.domain.repositories.TasksRepository
import java.util.*

class GetMainTasks(
    private val repository: TasksRepository
) : UseCase<List<Task>, Date>() {
    override suspend fun run(params: Date): Either<Failure, List<Task>> {

        return when (val result = repository.getTasks()) {
            is Either.Right -> {
                Either.Right(result.value.filter { it.status != 2 }.take(5))
            }
            else -> result
        }
    }
}