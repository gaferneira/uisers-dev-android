package co.tuister.data.repositories

import co.tuister.data.dto.EventDto
import co.tuister.data.utils.BaseCollection
import co.tuister.domain.entities.Event
import co.tuister.domain.repositories.CalendarRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class CalendarRepositoryImpl(
    val db: FirebaseFirestore,
    private val gson: Gson
) : CalendarRepository {

    private val baseCollection by lazy { BaseCollection(db) }

    override suspend fun getEvents(): List<Event> {
        val document = baseCollection.getCalendarDocument()
        val field = document?.get(BaseCollection.FIELD_CALENDAR)
        val json = gson.toJson(field)
        return gson.fromJson(json, Array<EventDto>::class.java).toList().map {
           Event(it.title, it.description, stringToDateTime(it.date)?.time ?: 0, it.duration)
        }
    }

    override suspend fun getEventsByDate(date: Date): List<Event> {
        val document = baseCollection.getCalendarDocument()
        val field = document?.get(BaseCollection.FIELD_CALENDAR)
        val json = gson.toJson(field)
        return gson.fromJson(json, Array<EventDto>::class.java).toList().filter {
            it.date.startsWith(DATE_TIME.format(date))
        }.map {
            Event(it.title, it.description, stringToDateTime(it.date)?.time ?: 0, it.duration)
        }
    }

    private fun stringToDateTime(string: String?): Date? {
        return string?.let {
            try {
                DATE_TIME_FORMAT.parse(it)
            } catch (e: Exception) {
                null
            }
        }
    }

    companion object {
        private val DATE_TIME_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm")
        private val DATE_TIME = SimpleDateFormat("yyyy-MM-dd")

    }
}
