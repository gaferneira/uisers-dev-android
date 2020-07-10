package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.SchedulePeriod

interface ScheduleRepository {
    suspend fun save(period: SchedulePeriod): Either<Failure, SchedulePeriod>
    suspend fun getSchedule(): Either<Failure, List<SchedulePeriod>>
    suspend fun remove(item: SchedulePeriod): Either<Failure, Boolean>
}
