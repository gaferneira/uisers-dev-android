package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Event
import java.util.*

interface CalendarRepository {
    suspend fun getEvents(): List<Event>
    suspend fun getEventsByDate(date: Date): List<Event>
}
