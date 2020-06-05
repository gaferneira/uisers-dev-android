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
        return Either.Right(
            listOf(
                Task("Tarea 1", "Descripcion 1", "08:00", "10:00"),
                Task("Tarea 2", "Descripcion 2", "10:00", "12:00"),
                Task("Tarea 3", "Descripcion 3", "14:00", "16:00")
            )
        )
    }

    override suspend fun getTasksByDate(date: Date): Either<Failure, List<Task>> {
        delay(1000)
        return Either.Right(
            listOf(
                Task("Tarea 1", "Descripcion 1", "08:00", "10:00"),
                Task("Tarea 2", "Descripcion 2", "10:00", "12:00"),
                Task("Tarea 3", "Descripcion 3", "14:00", "16:00")
            )
        )
    }

}