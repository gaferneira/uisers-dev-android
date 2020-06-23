package co.tuister.domain.usecases.profile

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.repositories.UserRepository

class FirstTimeUseCase(
    private val userRepository: UserRepository
) : NoParamsUseCase<Boolean> {
    override suspend fun run(): Either<Failure, Boolean> {
        val result = userRepository.checkFirstTime()
        return Either.Right(result)
    }
}
