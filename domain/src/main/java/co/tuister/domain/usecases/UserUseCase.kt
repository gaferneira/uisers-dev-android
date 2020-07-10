package co.tuister.domain.usecases

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.UserRepository

class UserUseCase(
    private val userRepository: UserRepository
) : NoParamsUseCase<User> {
    override suspend fun invoke(): Either<Failure, User> {
        return try {
            userRepository.getUser()
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
