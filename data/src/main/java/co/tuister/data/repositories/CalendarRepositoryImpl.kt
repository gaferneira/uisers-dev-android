package co.tuister.data.repositories

import co.tuister.data.dto.EventDto
import co.tuister.data.utils.BaseCollection
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Event
import co.tuister.domain.repositories.CalendarRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CalendarRepositoryImpl(
    val db: FirebaseFirestore,
    private val gson: Gson,
    private val connectivityUtil: ConnectivityUtil
) : BaseRepositoryImpl(), CalendarRepository {

    private val baseCollection by lazy { BaseCollection(db, connectivityUtil) }

    override suspend fun getEvents(): Either<Failure, List<Event>> {
        return eitherResult {
            val document = baseCollection.getCalendarDocument()
            val field = document?.get(BaseCollection.FIELD_CALENDAR)
            val json = gson.toJson(field)
            gson.fromJson(json, Array<EventDto>::class.java).toList().map {
                Event(it.title, it.description, stringToDateTime(it.date) ?: Date(0), it.duration)
            }
        }
    }

    override suspend fun getUpcomingEvents(date: Date): Either<Failure, List<Event>> {
        return eitherResult {
            val document = baseCollection.getCalendarDocument()
            val field = document?.get(BaseCollection.FIELD_CALENDAR)
            val json = gson.toJson(field)
            val stringDate = DATE_TIME.format(date)
            gson.fromJson(json, Array<EventDto>::class.java).toList().filter {
                it.date >= stringDate
            }.take(MAX_EVENTS).map {
                Event(it.title, it.description, stringToDateTime(it.date) ?: Date(0), it.duration)
            }
        }
    }

    private fun stringToDateTime(string: String?): Date? {
        return string?.let {
            try {
                DATE_TIME_FORMAT.parse(it)
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }
    }

    companion object {
        private val DATE_TIME_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm")
        private val DATE_TIME = SimpleDateFormat("yyyy-MM-dd")
        private const val MAX_EVENTS = 3
    }
}
