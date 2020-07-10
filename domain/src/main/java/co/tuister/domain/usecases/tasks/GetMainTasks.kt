package co.tuister.domain.usecases.tasks

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.base.getOrElse
import co.tuister.domain.entities.Task
import co.tuister.domain.repositories.TasksRepository
import java.util.*

class GetMainTasks(
    private val repository: TasksRepository
) : UseCase<List<Task>, Date> {
    override suspend fun invoke(params: Date): Either<Failure, List<Task>> {
        return try {
            val result = repository.getTasks().getOrElse(listOf())
            Either.Right(
                result
                    .sortedByDescending { it.status }
                    .filter { it.status != Task.STATUS_DONE }
                    .take(MAX_ROWS)
            )
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }

    companion object {
        private const val MAX_ROWS = 5
    }
}
