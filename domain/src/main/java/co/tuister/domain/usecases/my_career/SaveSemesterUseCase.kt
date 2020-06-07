package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Either.Right
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Semester
import co.tuister.domain.repositories.SemesterRepository

class SaveSemesterUseCase(
    private val repository: SemesterRepository
) : UseCase<Semester, Semester>() {
    override suspend fun run(params: Semester): Either<Failure, Semester> {
        return when (val result = repository.save(params)) {
            is Either.Left -> result
            is Right -> {
                Right(result.value)
            }
        }
    }
}