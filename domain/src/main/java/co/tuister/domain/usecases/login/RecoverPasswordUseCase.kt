package co.tuister.domain.usecases.login

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Left
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.repositories.LoginRepository

class RecoverPasswordUseCase(
    private val loginRepository: LoginRepository
) : UseCase<Boolean, String> {
    override suspend fun invoke(params: String): Either<Failure, Boolean> {
        return try {
            loginRepository.recoverPassword(params)
        } catch (e: Exception) {
            Left(Failure.analyzeException(e))
        }
    }
}
