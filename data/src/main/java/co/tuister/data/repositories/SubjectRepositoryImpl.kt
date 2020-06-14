package co.tuister.data.repositories

import co.tuister.data.await
import co.tuister.data.dto.*
import co.tuister.data.utils.BaseCollection
import co.tuister.data.utils.BaseCollection.Companion.FIELD_SUBJECTS
import co.tuister.data.utils.SemestersCollection
import co.tuister.data.utils.objectToMap
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.CareerSubject
import co.tuister.domain.entities.Note
import co.tuister.domain.entities.Subject
import co.tuister.domain.repositories.SubjectRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class SubjectRepositoryImpl(
    firebaseAuth: FirebaseAuth,
    db: FirebaseFirestore,
    val gson: Gson
) : MyCareerRepository(firebaseAuth, db), SubjectRepository {

    private val baseCollection by lazy { BaseCollection(db) }

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
        return try {

            val subjects = semestersCollection.documentByPath(getCurrentSemesterPath())
                .collection(SemestersCollection.COL_SUBJECTS)
                .get()
                .await()!!
                .documents.map {
                    val path = it.reference.path
                    it.toObject(SubjectUserDto::class.java)!!.toEntity().apply {
                        id = path
                    }
                }

            Either.Right(subjects)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }


    override suspend fun save(subject: Subject): Either<Failure, Subject> {
        return try {
            if (subject.id.isNotEmpty()){
                val subjectDto = semestersCollection.documentByPath(subject.id).get().await()!!
                subjectDto.reference.update(subject.toDTO().objectToMap())
            } else {
                val id = semestersCollection.documentByPath(getCurrentSemesterPath())
                    .collection(SemestersCollection.COL_SUBJECTS)
                    .add(subject.toDTO())
                    .await()!!.path
                subject.id = id
            }

            Either.Right(subject)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }


    override suspend fun getNotes(subject: Subject): Either<Failure, List<Note>> {
        return try {

            val notes = semestersCollection.documentByPath(subject.id)
                .collection(SemestersCollection.COL_NOTES)
                .get().await()!!
                .documents
                .map {
                    val path = it.reference.path
                    it.toObject(NoteDto::class.java)!!.toEntity().apply {
                        id = path
                } }

            Either.Right(notes)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    override suspend fun saveNote(note: Note, subject: Subject): Either<Failure, Note> {
            return try {
                if (note.id.isNotEmpty()){
                    val noteDto = semestersCollection.documentByPath(note.id).get().await()!!
                    noteDto.reference.update(note.toDTO().objectToMap())
                } else {
                    val id = semestersCollection.documentByPath(subject.id)
                        .collection(SemestersCollection.COL_NOTES)
                        .add(note.toDTO())
                        .await()!!.path
                    note.id = id
                }

                Either.Right(note)
            } catch (exception: Exception) {
                Either.Left(Failure.ServerError(exception))
            }
    }

}

