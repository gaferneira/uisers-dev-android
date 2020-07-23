package co.tuister.domain.usecases.institutional

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.base.map
import co.tuister.domain.entities.Event
import co.tuister.domain.repositories.CalendarRepository
import java.util.Calendar
import java.util.Date

class GetUpcomingEventsUseCase(
    private val repository: CalendarRepository
) : UseCase<List<Event>, Date> {
    override suspend fun invoke(params: Date): Either<Failure, List<Event>> {
        return try {
            repository.getUpcomingEvents(params).map { list ->
                val maxDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, MAX_DAYS)
                }.time
                list.filter { it.date < maxDate }
            }
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }

    companion object {
        const val MAX_DAYS = 30
    }
}
