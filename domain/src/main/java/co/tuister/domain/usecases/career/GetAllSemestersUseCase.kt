package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Semester
import co.tuister.domain.repositories.SemesterRepository

class GetAllSemestersUseCase(
    private val repository: SemesterRepository
) : NoParamsUseCase<List<Semester>> {

    override suspend fun run(): Either<Failure, List<Semester>> {
        return try {
            Either.Right(repository.getAll())
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
