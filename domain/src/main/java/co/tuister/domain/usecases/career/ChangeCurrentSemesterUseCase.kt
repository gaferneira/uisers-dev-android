package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Left
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Semester
import co.tuister.domain.repositories.SemesterRepository

class ChangeCurrentSemesterUseCase(
    private val repository: SemesterRepository
) : UseCase<Semester, Semester> {
    override suspend fun invoke(params: Semester): Either<Failure, Semester> {
        return try {
            repository.changeCurrentSemester(params)
        } catch (e: Exception) {
            Left(Failure.analyzeException(e))
        }
    }
}
