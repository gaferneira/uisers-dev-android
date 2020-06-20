package co.tuister.domain.usecases.my_career

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.UseCase
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject
import co.tuister.domain.repositories.SubjectRepository

class SaveNoteUseCase(
    private val repository: SubjectRepository
) : UseCase<Note, Pair<Note, Subject>>() {
    override suspend fun run(params: Pair<Note, Subject>): Either<Failure, Note> {
        return try {
            Either.Right(repository.saveNote(params.first, params.second))
        } catch (e: Exception) {
            Either.Left(analyzeException(e))
        }
    }
}
