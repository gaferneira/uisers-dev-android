package co.tuister.data.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Semester
import co.tuister.domain.repositories.SemesterRepository
import kotlinx.coroutines.delay

class SemesterRepositoryImpl : SemesterRepository {
    override suspend fun getCurrent(): Either<Failure, Semester> {
        delay(1000)
        return Either.Right(Semester(2019, 2, 3.7f))
    }
}