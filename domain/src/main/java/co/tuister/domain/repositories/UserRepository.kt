package co.tuister.domain.repositories

import android.net.Uri
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Career
import co.tuister.domain.entities.User

interface UserRepository {
    suspend fun getUser(): Either<Failure, User>
    suspend fun getCareers(): Either<Failure, List<Career>>
    suspend fun downloadImage(email:String): Either<Failure, Uri>
}