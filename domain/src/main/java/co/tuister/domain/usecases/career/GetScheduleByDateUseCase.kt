package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.base.getOrElse
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.repositories.ScheduleRepository
import java.util.*

class GetScheduleByDateUseCase(
    private val repository: ScheduleRepository
) : UseCase<List<SchedulePeriod>, Date> {

    override suspend fun invoke(params: Date): Either<Failure, List<SchedulePeriod>> {
        return try {
            val day = Calendar.getInstance().apply {
                time = params
            }.get(Calendar.DAY_OF_WEEK)
            val result = repository.getSchedule()
            if (result.isRight) {
                val list = result.getOrElse(listOf())
                Either.Right(list.filter { it.day == day }.sortedBy { it.initialHour })
            } else {
                result
            }
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
