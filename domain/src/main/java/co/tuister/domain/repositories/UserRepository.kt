package co.tuister.domain.repositories

import android.net.Uri
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Career
import co.tuister.domain.entities.User

interface UserRepository {
    suspend fun getUser(): Either<Failure, User>
    suspend fun getCareers(): List<Career>
    suspend fun getCampus(): List<String>
    suspend fun downloadImage(email: String): Either<Failure, Uri>
    suspend fun updateUser(user: User): Either<Failure, Boolean>
    suspend fun updateFCMToken(): Boolean
    suspend fun reSendVerifyEmail(): Boolean
    suspend fun sendFeedback(comment: String): Boolean
    suspend fun checkFirstTime(): Boolean
    suspend fun disableFirstTime()
}
