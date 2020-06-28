package co.tuister.domain.repositories

import co.tuister.domain.entities.Event
import java.util.*

interface CalendarRepository {
    suspend fun getEvents(): List<Event>
    suspend fun getUpcomingEvents(date: Date): List<Event>
}
