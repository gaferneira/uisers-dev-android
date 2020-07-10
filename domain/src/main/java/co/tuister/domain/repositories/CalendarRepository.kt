package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Event
import java.util.*

interface CalendarRepository {
    suspend fun getEvents(): Either<Failure, List<Event>>
    suspend fun getUpcomingEvents(date: Date): Either<Failure, List<Event>>
}
