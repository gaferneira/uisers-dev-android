package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Semester
import co.tuister.domain.repositories.SemesterRepository

class SaveSemesterUseCase(
    private val repository: SemesterRepository
) : UseCase<Semester, Semester> {
    override suspend fun invoke(params: Semester): Either<Failure, Semester> {
        return try {
            repository.save(params)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
