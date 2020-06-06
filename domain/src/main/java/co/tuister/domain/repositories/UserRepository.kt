package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User

interface UserRepository {
    suspend fun getUser(): Either<Failure, User>
}