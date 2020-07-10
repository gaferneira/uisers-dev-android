package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Semester

interface SemesterRepository {
    suspend fun getCurrent(): Either<Failure, Semester>
    suspend fun getAll(): Either<Failure, List<Semester>>
    suspend fun save(semester: Semester): Either<Failure, Semester>
    suspend fun changeCurrentSemester(semester: Semester): Either<Failure, Semester>
    suspend fun remove(item: Semester): Either<Failure, Boolean>
}
