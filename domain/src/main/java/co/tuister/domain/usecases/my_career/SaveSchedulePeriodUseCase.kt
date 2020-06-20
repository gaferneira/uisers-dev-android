package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Right
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.repositories.ScheduleRepository

class SaveSchedulePeriodUseCase(
    private val repository: ScheduleRepository
) : UseCase<SchedulePeriod, SchedulePeriod>() {
    override suspend fun run(params: SchedulePeriod): Either<Failure, SchedulePeriod> {
        return try {
            Right(repository.save(params))
        }
        catch (e: Exception) {
            Either.Left(analyzeException(e))
        }
    }
}
