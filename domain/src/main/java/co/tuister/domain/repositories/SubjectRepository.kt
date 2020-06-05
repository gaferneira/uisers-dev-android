package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject
import co.tuister.domain.entities.SubjectClass
import java.util.*

interface SubjectRepository {
    suspend fun save(subject: Subject): Either<Failure, Subject>
    suspend fun getAll(): Either<Failure, List<Subject>>
    suspend fun getMySubjects(): Either<Failure, List<Subject>>
    suspend fun getScheduleByDate(date: Date): Either<Failure, List<SubjectClass>>
    suspend fun getNotes(subject: Subject): Either<Failure, List<Note>>
}