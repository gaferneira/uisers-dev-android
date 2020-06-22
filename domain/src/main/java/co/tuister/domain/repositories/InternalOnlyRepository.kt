package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User

interface InternalOnlyRepository {
    suspend fun loadDataCareers(): Either<Failure, Boolean>
    suspend fun loadDataSubjects(): Either<Failure, Boolean>
    suspend fun getAllUserData(): Either<Failure, List<User>>
    suspend fun loadMapData(): Either<Failure, Boolean>
    suspend fun loadDataCalendar(): Either<Failure, Boolean>
}
