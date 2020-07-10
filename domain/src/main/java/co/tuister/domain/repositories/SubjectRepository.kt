package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.CareerSubject
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject

interface SubjectRepository {
    suspend fun save(subject: Subject): Either<Failure, Subject>
    suspend fun getAll(): Either<Failure, List<CareerSubject>>
    suspend fun getMySubjects(): Either<Failure, List<Subject>>
    suspend fun remove(subject: Subject): Either<Failure, Boolean>
    // Notes
    suspend fun saveNote(item: Note, subject: Subject): Either<Failure, Note>
    suspend fun getNotes(subject: Subject): Either<Failure, List<Note>>
    suspend fun removeNote(item: Note): Either<Failure, Boolean>
}
