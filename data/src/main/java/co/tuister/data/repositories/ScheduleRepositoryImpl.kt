package co.tuister.data.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.repositories.ScheduleRepository
import kotlinx.coroutines.delay
import java.util.*

class ScheduleRepositoryImpl() : ScheduleRepository {

    override suspend fun save(period: SchedulePeriod): Either<Failure, SchedulePeriod> {
        delay(1000)
        return Either.Right(period)
    }


    override suspend fun getScheduleByDate(date: Date): Either<Failure, List<SchedulePeriod>> {
        delay(1000)
        return Either.Right(
            listOf(
                SchedulePeriod("Calculo I",  1,"08:00", "10:00", "CT 301"),
                SchedulePeriod("Quimica",1, "10:00", "12:00", "LP 310")
            )
        )
    }

    override suspend fun getSchedule(): Either<Failure, List<SchedulePeriod>> {

        delay(1000)
        return Either.Right(
            listOf(
                SchedulePeriod("Calculo I", 2, "10:00", "12:00", "LP 310"),
                SchedulePeriod("Fisica I", 2, "12:00", "14:00", "LP 310"),
                SchedulePeriod("Biologia", 2, "08:00", "9:00", "LP 310"),
                SchedulePeriod("Quimica", 3, "10:00", "12:00", "LP 310"),
                SchedulePeriod("Quimica", 3, "10:00", "12:00", "LP 310"),
                SchedulePeriod("Fisica I", 4, "10:00", "12:00", "LP 310"),
                SchedulePeriod("Quimica",5, "10:00", "12:00", "LP 310"),
                SchedulePeriod("Calculo I",5, "08:00", "10:00", "CT 301"),
                SchedulePeriod("Fundamentos", 6, "10:00", "12:00", "LP 310")
            )
        )
    }

}