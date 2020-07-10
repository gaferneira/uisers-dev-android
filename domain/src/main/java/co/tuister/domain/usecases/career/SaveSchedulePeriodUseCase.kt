package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.repositories.ScheduleRepository

class SaveSchedulePeriodUseCase(
    private val repository: ScheduleRepository
) : UseCase<SchedulePeriod, SchedulePeriod> {
    override suspend fun invoke(params: SchedulePeriod): Either<Failure, SchedulePeriod> {
        return try {
            repository.save(params)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
