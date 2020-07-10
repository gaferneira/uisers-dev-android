package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Subject
import co.tuister.domain.repositories.SubjectRepository

class GetMySubjectsUseCase(
    private val repository: SubjectRepository
) : NoParamsUseCase<List<Subject>> {
    override suspend fun invoke(): Either<Failure, List<Subject>> {
        return try {
            repository.getMySubjects()
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
