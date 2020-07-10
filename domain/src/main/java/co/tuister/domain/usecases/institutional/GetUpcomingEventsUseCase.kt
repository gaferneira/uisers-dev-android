package co.tuister.domain.usecases.institutional

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Event
import co.tuister.domain.repositories.CalendarRepository
import java.util.Date

class GetUpcomingEventsUseCase(
    private val repository: CalendarRepository
) : UseCase<List<Event>, Date> {
    override suspend fun invoke(params: Date): Either<Failure, List<Event>> {
        return try {
            repository.getUpcomingEvents(params)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
