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

    override suspend fun getAll(): Either<Failure, List<Semester>> {
        delay(1000)
        return Either.Right(
            listOf(
                Semester(2019, 2, 4.2f, 35, true),
                Semester(2019, 1, 4.2f, 34),
                Semester(2018, 2, 4.1f, 40),
                Semester(2018, 1, 3.9f,38),
                Semester(2017, 2, 3.7f,40),
                Semester(2017, 1, 3.8f, 38)
            ))
    }

    override suspend fun save(semester: Semester): Either<Failure, Semester> {
        delay(1000)
        return Either.Right(semester)
    }

    override suspend fun changeCurrentSemester(semester: Semester): Either<Failure, Semester> {
        delay(1001)
        return Either.Right(semester)
    }
}
