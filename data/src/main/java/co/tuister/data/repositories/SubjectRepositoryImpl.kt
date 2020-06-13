package co.tuister.data.repositories

import co.tuister.data.utils.BaseCollection
import co.tuister.data.utils.BaseCollection.Companion.FIELD_SUBJECTS
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.CareerSubject
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject
import co.tuister.domain.repositories.SubjectRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.delay

class SubjectRepositoryImpl(
    private val db: FirebaseFirestore,
    private val gson: Gson
) : SubjectRepository {

    private val baseCollection by lazy { BaseCollection(db) }

    override suspend fun save(subject: Subject): Either<Failure, Subject> {
        delay(1000)
        return Either.Right(subject)
    }

    override suspend fun getAll(): Either<Failure, List<CareerSubject>> {

        return try {
            val document = baseCollection.getBaseDocument()
            val field = document?.get(FIELD_SUBJECTS)
            val json = gson.toJson(field)
            val list = gson.fromJson(json, Array<CareerSubject>::class.java).toList()
            return Either.Right(list)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
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

