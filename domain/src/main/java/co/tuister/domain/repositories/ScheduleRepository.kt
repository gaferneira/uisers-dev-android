package co.tuister.domain.repositories

import co.tuister.domain.entities.SchedulePeriod

interface ScheduleRepository {
    suspend fun save(period: SchedulePeriod): SchedulePeriod
    suspend fun getSchedule(): List<SchedulePeriod>
    suspend fun remove(item: SchedulePeriod): Boolean
}
