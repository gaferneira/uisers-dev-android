package co.tuister.domain.usecases.login

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Left
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.LoginRepository

class LoginUseCase(
    private val loginRepository: LoginRepository
) : UseCase<User, LoginUseCase.Params> {
    override suspend fun invoke(params: Params): Either<Failure, User> {
        return try {
            loginRepository.login(params.email, params.password)
        } catch (e: Exception) {
            Left(Failure.analyzeException(e))
        }
    }

    data class Params(val email: String, val password: String)
}
