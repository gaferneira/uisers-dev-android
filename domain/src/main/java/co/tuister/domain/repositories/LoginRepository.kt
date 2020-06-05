package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User

interface LoginRepository {
    suspend fun login(email: String, password: String): Either<Failure, User?>
    suspend fun logout()
    suspend fun hasSessionActive(): Boolean
    suspend fun recoverPassword(email: String): Boolean
    suspend fun register(user: User, password: String): Either<Failure, Boolean>
}