package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Right
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Subject
import co.tuister.domain.repositories.SubjectRepository

class RemoveSubjectUseCase(
    private val repository: SubjectRepository
) : UseCase<Boolean, Subject>() {
    override suspend fun run(params: Subject): Either<Failure, Boolean> {
        return try {
            Right(repository.remove(params))
        } catch (e: Exception) {
            Either. Left(Failure.analyzeException(e))
        }
    }
}
