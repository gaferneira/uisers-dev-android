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
        return when (val result = repository.save(params)) {
            is Either.Left -> result
            is Right -> {
                Right(result.value)
            }
        }
    }
}