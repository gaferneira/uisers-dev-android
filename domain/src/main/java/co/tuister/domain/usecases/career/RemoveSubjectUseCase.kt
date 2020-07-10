package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Subject
import co.tuister.domain.repositories.SubjectRepository

class RemoveSubjectUseCase(
    private val repository: SubjectRepository
) : UseCase<Boolean, Subject> {
    override suspend fun invoke(params: Subject): Either<Failure, Boolean> {
        return try {
            repository.remove(params)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
