package co.tuister.domain.usecases.login

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Left
import co.tuister.domain.base.Either.Right
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.repositories.UserRepository

class SendVerifyLinkUseCase(
    private val userRepository: UserRepository
) : NoParamsUseCase<Boolean>() {
    override suspend fun run(): Either<Failure, Boolean> {
        val result = userRepository.reSendVerifyEmail()
        return if (result) {
            Left(Failure.ServerError())
        } else {
            Right(true)
        }
    }
}