package co.tuister.domain.usecases.career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Note
import co.tuister.domain.repositories.SubjectRepository

class RemoveNoteUseCase(
    private val repository: SubjectRepository
) : UseCase<Boolean, Note> {
    override suspend fun invoke(params: Note): Either<Failure, Boolean> {
        return try {
            repository.removeNote(params)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
