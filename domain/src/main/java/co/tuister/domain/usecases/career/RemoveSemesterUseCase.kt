package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Right
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Semester
import co.tuister.domain.repositories.SemesterRepository

class RemoveSemesterUseCase(
    private val repository: SemesterRepository
) : UseCase<Boolean, Semester> {
    override suspend fun run(params: Semester): Either<Failure, Boolean> {
        return try {
            Right(repository.remove(params))
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
