package co.tuister.domain.usecases.internal

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.InternalOnlyRepository

class DataUserUseCase(
    private val internalOnlyRepository: InternalOnlyRepository
) : NoParamsUseCase<List<User>> {
    override suspend fun invoke(): Either<Failure, List<User>> {
        return internalOnlyRepository.getAllUserData()
    }
}
