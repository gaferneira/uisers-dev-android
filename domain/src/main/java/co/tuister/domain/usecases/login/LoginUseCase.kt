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
) : UseCase<Boolean, LoginUseCase.Params>() {
    override suspend fun run(params: Params): Either<Failure, Boolean> {
        val result = loginRepository.login(params.email, params.password)
        return when (result) {
            is Left -> result
            is Right -> {
                result.value?.let { createSession(it) }
                Right(result.value != null)
            }
        }
    }

    private fun createSession(user: User) {
        Session.user = user
    }

    data class Params(val email: String, val password: String)

}