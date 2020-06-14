package co.tuister.data.repositories

import co.tuister.data.dto.DataSemesterUserDto
import co.tuister.data.dto.toDTO
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.SemestersCollection
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Semester
import co.tuister.domain.repositories.SemesterRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SemesterRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : SemesterRepository {

    private val semestersCollection by lazy { SemestersCollection(db) }

    override suspend fun getCurrent(): Either<Failure, Semester> {
        return try {
            val data = getData()
            val semester = data.semesters.firstOrNull {it.current} ?: data.semesters[0]
            Either.Right(semester.toEntity())
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }

    override suspend fun getAll(): Either<Failure, List<Semester>> {
        return try {
            val data = getData()
            Either.Right(data.semesters.map {
                it.toEntity()
            })
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    override suspend fun save(semester: Semester): Either<Failure, Semester> {
        return try {
            val data = getData()
            data.semesters.add(semester.toDTO())
            saveData(data)
            Either.Right(semester)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    override suspend fun changeCurrentSemester(semester: Semester): Either<Failure, Semester> {
        return try {
            val data = getData()
            data.semesters.forEach { it.current = false }
            data.semesters.firstOrNull { it.period == semester.period }?.current = true
            saveData(data)
            Either.Right(semester)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }


    private suspend fun getData() : DataSemesterUserDto {
        val email = firebaseAuth.currentUser!!.email!!
        val doc = semestersCollection.getAll(email)!!.documents.firstOrNull()
        return if (doc == null) {
            val semester = Semester("2020-1", 0f, 0, true)
            val data = DataSemesterUserDto("0", true, email, mutableListOf(semester.toDTO()))
            semestersCollection.create(data)
            data
        } else {
            doc.toObject(DataSemesterUserDto::class.java)!!
        }
    }

    private suspend fun saveData(data : DataSemesterUserDto)  {
        val email = firebaseAuth.currentUser!!.email!!
        val docs = semestersCollection.getAll(email)!!.documents
        if (docs.isEmpty()) {
            semestersCollection.create(data)
        } else {
            semestersCollection.update(data, docs.first().id)
        }
    }
}
