package co.tuister.data.repositories

import co.tuister.data.utils.await
import co.tuister.data.dto.*
import co.tuister.data.utils.BaseCollection
import co.tuister.data.utils.BaseCollection.Companion.FIELD_SUBJECTS
import co.tuister.data.utils.SemestersCollection
import co.tuister.data.utils.objectToMap
import co.tuister.domain.base.Either
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
    private val gson: Gson
) : MyCareerRepository(firebaseAuth, db), SubjectRepository {

    private val baseCollection by lazy { BaseCollection(db) }

    override suspend fun getAll(): List<CareerSubject> {
        val document = baseCollection.getBaseDocument()
        val field = document?.get(FIELD_SUBJECTS)
        val json = gson.toJson(field)
        return gson.fromJson(json, Array<CareerSubject>::class.java).toList()
    }

    override suspend fun getMySubjects(): List<Subject> {
        val subjects = semestersCollection.documentByPath(getCurrentSemesterPath())
            .collection(SemestersCollection.COL_SUBJECTS)
            .get()
            .await()!!
            .documents.map {
                val path = it.reference.path
                it.toObject(SubjectUserDto::class.java)!!.toEntity(path)
            }

        return subjects.sortedByDescending { it.credits }
    }


    override suspend fun save(subject: Subject): Subject {
        if (subject.id.isNotEmpty()) {
            val subjectDto = semestersCollection.documentByPath(subject.id).get().await()!!
            subjectDto.reference.update(subject.toDTO().objectToMap())
        } else {
            val id = semestersCollection.documentByPath(getCurrentSemesterPath())
                .collection(SemestersCollection.COL_SUBJECTS)
                .add(subject.toDTO())
                .await()!!.path
            subject.id = id
        }

        return subject
    }


    override suspend fun getNotes(subject: Subject): List<Note> {
        return semestersCollection.documentByPath(subject.id)
            .collection(SemestersCollection.COL_NOTES)
            .get().await()!!
            .documents
            .map {
                val path = it.reference.path
                it.toObject(NoteDto::class.java)!!.toEntity(path)
            }
    }

    override suspend fun saveNote(note: Note, subject: Subject): Note {
        if (note.id.isNotEmpty()) {
            val noteDto = semestersCollection.documentByPath(note.id).get().await()!!
            noteDto.reference.update(note.toDTO().objectToMap())
        } else {
            val id = semestersCollection.documentByPath(subject.id)
                .collection(SemestersCollection.COL_NOTES)
                .add(note.toDTO())
                .await()!!.path
            note.id = id
        }

        return note
    }

}

