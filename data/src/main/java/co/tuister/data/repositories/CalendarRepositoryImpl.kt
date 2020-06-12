package co.tuister.data.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Event
import co.tuister.domain.repositories.CalendarRepository
import kotlinx.coroutines.delay
import java.util.*

class CalendarRepositoryImpl : CalendarRepository {

    override suspend fun getEvents(): Either<Failure, List<Event>> {
        delay(1000)
        val calendar = Calendar.getInstance()
        calendar.set(2020, 0, 1)

        val list = mutableListOf<Event>()
        for (i in 0..366) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            list.add(Event("Event A$i", "Description A", calendar.timeInMillis, 0, true))
            list.add(Event("Event B$i", "Description B", calendar.timeInMillis, 0, true))
            list.add(Event("Event C$i", "Description C", calendar.timeInMillis, 0, true))
        }
        return Either.Right(list)
    }

    override suspend fun getEventsByDate(date: Date): Either<Failure, List<Event>> {
        delay(1000)
        val calendar = Calendar.getInstance()
        calendar.time = date
        val list = mutableListOf<Event>()
        for (i in 0..10) {
            list.add(Event("Event $i", "Description", calendar.timeInMillis, 0, true))
        }
        return Either.Right(list)
    }

}