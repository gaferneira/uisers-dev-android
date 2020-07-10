package co.tuister.domain.usecases.profile

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.repositories.UserRepository

class DisableFirstTimeUseCase(
    private val userRepository: UserRepository
) : NoParamsUseCase<Boolean> {
    override suspend fun invoke(): Either<Failure, Boolean> {
        return try {
            Either.Right(userRepository.disableFirstTime())
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
