package co.tuister.domain.usecases.institutional

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Event
import co.tuister.domain.repositories.CalendarRepository

class GetEventsUseCase(
    private val repository: CalendarRepository
) : NoParamsUseCase<List<Event>>() {
    override suspend fun run(): Either<Failure, List<Event>> {
        return try {
            Either.Right(repository.getEvents())
        }
        catch (e: Exception) {
            Either. Left(Failure.analyzeException(e))
        }
    }
}
