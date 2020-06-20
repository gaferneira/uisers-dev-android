package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.repositories.ScheduleRepository
import java.util.*

class GetScheduleByDateUseCase(
    private val repository: ScheduleRepository
) : UseCase<List<SchedulePeriod>, Date>() {

    override suspend fun run(params: Date): Either<Failure, List<SchedulePeriod>> {
        return try {
            val day = Calendar.getInstance().apply {
                time = params
            }.get(Calendar.DAY_OF_WEEK)
            val list = repository.getSchedule()
            Either.Right(list.filter { it.day == day }.sortedBy { it.initialHour })
        }
        catch (e: Exception) {
            Either.Left(analyzeException(e))
        }
    }

}
