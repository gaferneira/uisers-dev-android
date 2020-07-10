package co.tuister.domain.usecases.login

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Left
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.repositories.UserRepository

class FCMUpdateUseCase(
    private val userRepository: UserRepository
) : NoParamsUseCase<Boolean> {
    override suspend fun invoke(): Either<Failure, Boolean> {
        return try {
            userRepository.updateFCMToken()
        } catch (e: Exception) {
            Left(Failure.analyzeException(e))
        }
    }
}
