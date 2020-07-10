package co.tuister.domain.usecases.login

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Left
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.LoginRepository

class RegisterUseCase(
    private val loginRepository: LoginRepository
) : UseCase<Boolean, RegisterUseCase.Params> {
    override suspend fun invoke(params: Params): Either<Failure, Boolean> {
        return try {
            loginRepository.register(params.user, params.password)
        } catch (e: Exception) {
            Left(Failure.analyzeException(e))
        }
    }

    data class Params(val user: User, val password: String)
}
