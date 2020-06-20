package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Subject
import co.tuister.domain.repositories.SubjectRepository

class GetMySubjectsUseCase(
    private val repository: SubjectRepository
) : NoParamsUseCase<List<Subject>>() {
    override suspend fun run(): Either<Failure, List<Subject>> {
        return try {
            Either.Right(repository.getMySubjects())
        } catch (e: Exception) {
            Either.Left(analyzeException(e))
        }
    }
}
