package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User

interface InternalOnlyRepository {
    suspend fun loadDataCareers()
    suspend fun loadDataSubjects()
    suspend fun getAllUserData(): Either<Failure, List<User>>
}