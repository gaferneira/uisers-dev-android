package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.repositories.ScheduleRepository

class RemoveSchedulePeriodUseCase(
    private val repository: ScheduleRepository
) : UseCase<Boolean, SchedulePeriod> {
    override suspend fun invoke(params: SchedulePeriod): Either<Failure, Boolean> {
        return try {
            repository.remove(params)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
