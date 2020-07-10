package co.tuister.domain.usecases.internal

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.repositories.InternalOnlyRepository

class UpdateDataSubjectsUseCase(
    private val internalOnlyRepository: InternalOnlyRepository
) : NoParamsUseCase<Boolean> {
    override suspend fun invoke(): Either<Failure, Boolean> {
        return internalOnlyRepository.loadDataSubjects()
    }
}
