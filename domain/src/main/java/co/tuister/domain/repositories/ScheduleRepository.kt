package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.SchedulePeriod
import java.util.*

interface ScheduleRepository {
    suspend fun save(period: SchedulePeriod): Either<Failure, SchedulePeriod>
    suspend fun getSchedule(): Either<Failure, List<SchedulePeriod>>
    suspend fun getScheduleByDate(date: Date): Either<Failure, List<SchedulePeriod>>
}