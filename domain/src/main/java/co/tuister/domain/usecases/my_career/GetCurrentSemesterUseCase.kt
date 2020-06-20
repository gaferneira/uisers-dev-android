package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Semester
import co.tuister.domain.repositories.SemesterRepository

class GetCurrentSemesterUseCase(
    private val repository: SemesterRepository
) : NoParamsUseCase<Semester>() {
    override suspend fun run(): Either<Failure, Semester> {
        return try {
            Either.Right(repository.getCurrent())
        } catch (e: Exception) {
            Either.Left(analyzeException(e))
        }
    }
}
