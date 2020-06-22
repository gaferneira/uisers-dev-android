package co.tuister.domain.usecases.login

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Left
import co.tuister.domain.base.Either.Right
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Session
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.LoginRepository

class LoginUseCase(
    private val loginRepository: LoginRepository
) : UseCase<Boolean, LoginUseCase.Params> {
    override suspend fun run(params: Params): Either<Failure, Boolean> {
        return try {
            val value = when (val result = loginRepository.login(params.email, params.password)) {
                is Right -> {
                    result.value?.also { createSession(it) }
                }
                else -> null
            }
            Right(value != null)
        } catch (e: Exception) {
            Left(Failure.analyzeException(e))
        }
    }

    private fun createSession(user: User) {
        Session.user = user
    }

    data class Params(val email: String, val password: String)
}
