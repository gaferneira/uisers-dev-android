package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject
import co.tuister.domain.repositories.SubjectRepository

class GetNotesUseCase(
    private val repository: SubjectRepository
) : UseCase<List<Note>, Subject>() {
    override suspend fun run(params: Subject): Either<Failure, List<Note>> {
        return try {
            Either.Right(repository.getNotes(params))
        } catch (e: Exception) {
            Either. Left(Failure.analyzeException(e))
        }
    }
}
