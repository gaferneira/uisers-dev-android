package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.CareerSubject
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject

interface SubjectRepository {
    suspend fun save(subject: Subject): Subject
    suspend fun getAll(): List<CareerSubject>
    suspend fun getMySubjects(): List<Subject>
    suspend fun getNotes(subject: Subject): List<Note>
    suspend fun saveNote(note: Note, subject: Subject): Note
}
