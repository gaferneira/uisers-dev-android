package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Right
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Subject
import co.tuister.domain.repositories.SubjectRepository

class SaveSubjectUseCase(
    private val repository: SubjectRepository
) : UseCase<Subject, Subject>() {
    override suspend fun run(params: Subject): Either<Failure, Subject> {
        return when (val result = repository.save(params)) {
            is Either.Left -> result
            is Right -> {
                Right(result.value)
            }
        }
    }


}