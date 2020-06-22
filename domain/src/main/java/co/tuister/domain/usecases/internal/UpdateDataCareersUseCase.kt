package co.tuister.domain.usecases.internal

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.repositories.InternalOnlyRepository

class UpdateDataCareersUseCase(
    private val internalOnlyRepository: InternalOnlyRepository
) : NoParamsUseCase<Boolean> {
    override suspend fun run(): Either<Failure, Boolean> {
        return internalOnlyRepository.loadDataCareers()
    }
}
