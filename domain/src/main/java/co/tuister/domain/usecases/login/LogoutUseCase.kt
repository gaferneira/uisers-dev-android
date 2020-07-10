package co.tuister.domain.usecases.login

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.repositories.LoginRepository

class LogoutUseCase(
    private val loginRepository: LoginRepository
) : NoParamsUseCase<Boolean> {
    override suspend fun invoke(): Either<Failure, Boolean> {
        return try {
            loginRepository.logout()
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
