package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Subject
import co.tuister.domain.repositories.SubjectRepository

class SaveSubjectUseCase(
    private val repository: SubjectRepository
) : UseCase<Subject, Subject> {
    override suspend fun invoke(params: Subject): Either<Failure, Subject> {
        return try {
            repository.save(params)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
