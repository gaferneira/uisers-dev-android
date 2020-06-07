package co.tuister.data.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Task
import co.tuister.domain.repositories.TasksRepository
import kotlinx.coroutines.delay
import java.util.*

class TasksRepositoryImpl : TasksRepository {

    override suspend fun getTasks(): Either<Failure, List<Task>> {
        delay(1000)

        val time = Calendar.getInstance().timeInMillis
        return Either.Right(
            listOf(
                Task("Tarea 1", "Descripcion 1", time),
                Task("Tarea 2", "Descripcion 2", time),
                Task("Tarea 3", "Descripcion 3", time),
                Task("Tarea 4", "Descripcion 4", time),
                Task("Tarea 5", "Descripcion 5", time),
                Task("Tarea 6", "Descripcion 6", time, 1),
                Task("Tarea 7", "Descripcion 7", time, 1, 0),
                Task("Tarea 8", "Descripcion 8", time, 1, 5),
                Task("Tarea 9", "Descripcion 9", time, 2, 10),
                Task("Tarea 10", "Descripcion 10", time, 2, 60)
            )
        )
    }

    override suspend fun getTasksByDate(date: Date): Either<Failure, List<Task>> {
        delay(1000)
        val time = Calendar.getInstance().timeInMillis
        return Either.Right(
            listOf(
                Task("Tarea 1", "Descripcion 1", time),
                Task("Tarea 2", "Descripcion 2", time),
                Task("Tarea 3", "Descripcion 3", time)
            )
        )
    }

    override suspend fun save(task: Task): Either<Failure, Task> {
        delay(1000)
        return Either.Right(task)
    }

}