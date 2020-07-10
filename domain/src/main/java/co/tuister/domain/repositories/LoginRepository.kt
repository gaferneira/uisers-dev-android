package co.tuister.domain.repositories

import android.net.Uri
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User

interface LoginRepository {
    suspend fun login(email: String, password: String): Either<Failure, User>
    suspend fun logout(): Either<Failure, Boolean>
    suspend fun hasSessionActive(): Either<Failure, Boolean>
    suspend fun recoverPassword(email: String): Either<Failure, Boolean>
    suspend fun register(user: User, password: String): Either<Failure, Boolean>
    suspend fun uploadImage(uri: Uri, email: String): Either<Failure, Boolean>
}
