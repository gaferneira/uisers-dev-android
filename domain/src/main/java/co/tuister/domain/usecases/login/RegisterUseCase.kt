package co.tuister.domain.usecases.login

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Left
import co.tuister.domain.base.Either.Right
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Session
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.LoginRepository

class RegisterUseCase(
    private val loginRepository: LoginRepository
) : UseCase<Boolean, RegisterUseCase.Params>() {
    override suspend fun run(params: Params): Either<Failure, Boolean> {
        val result = loginRepository.register(params.user, params.password)
        return when (result) {
            is Left -> result
            is Right -> {
                result.value?.let { createSession(params.user) }
                Right(result.value != null)
            }
        }
    }

    private fun createSession(user: User) {
        Session.user = user
    }

    data class Params(val user: User, val password: String)

}