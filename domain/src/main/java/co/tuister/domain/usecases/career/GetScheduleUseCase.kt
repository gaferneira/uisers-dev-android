package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.repositories.ScheduleRepository

class GetScheduleUseCase(
    private val repository: ScheduleRepository
) : NoParamsUseCase<List<SchedulePeriod>> {
    override suspend fun invoke(): Either<Failure, List<SchedulePeriod>> {
        return try {
            repository.getSchedule()
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
