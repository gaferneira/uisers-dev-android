package co.tuister.data.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject
import co.tuister.domain.entities.SubjectClass
import co.tuister.domain.repositories.SubjectRepository
import kotlinx.coroutines.delay
import java.util.*

class SubjectRepositoryImpl : SubjectRepository {

    override suspend fun save(subject: Subject): Either<Failure, Subject> {
        delay(1000)
        return Either.Right(subject)
    }

    override suspend fun getAll(): Either<Failure, List<Subject>> {

        delay(1000)
        return Either.Right(
            listOf(
                Subject("Calculo I", credits = 4),
                Subject("Calculo II", credits = 2),
                Subject("Calculo III", credits = 3),
                Subject("Quimica Basica", credits = 4),
                Subject("Fisica I", credits = 4),
                Subject("Fisica II", credits = 2),
                Subject("Fisica III", credits = 3)
            )
        )
    }

    override suspend fun getMySubjects(): Either<Failure, List<Subject>> {
        delay(1000)
        return Either.Right(
            listOf(
                Subject("Calculo I", "Higuera", 4f),
                Subject("Quimica", "Perez", 3.5f)
            )
        )
    }

    override suspend fun getScheduleByDate(date: Date): Either<Failure, List<SubjectClass>> {
        delay(1000)
        return Either.Right(
            listOf(
                SubjectClass(Subject("Calculo I", "", 0f), "08:00", "10:00", "CT 301"),
                SubjectClass(Subject("Quimica", "", 0f), "10:00", "12:00", "LP 310")
            )
        )
    }

    override suspend fun getNotes(subject: Subject): Either<Failure, List<Note>> {
        delay(1000)
        return Either.Right(
            listOf(
                Note("Parcial I", 3.8f, 20f, 0.76f),
                Note("Parcial II", 3.0f, 20f, 0.60f),
                Note("Parcial III", 2.0f, 20f, 0.40f),
                Note("Quices", 4.0f, 20f, 0.80f)
            )
        )
    }

    override suspend fun saveNote(note: Note, subject: Subject): Either<Failure, Note> {
        delay(1000)
        return Either.Right(note)
    }

}