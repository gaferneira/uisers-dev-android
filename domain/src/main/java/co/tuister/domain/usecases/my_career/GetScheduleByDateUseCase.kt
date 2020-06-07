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
        return repository.getScheduleByDate(params)
    }

}