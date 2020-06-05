package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Semester

interface SemesterRepository {
    suspend fun getCurrent(): Either<Failure, Semester>
}