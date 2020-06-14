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
import kotlinx.coroutines.delay

class SemesterRepositoryImpl(
    val firebaseAuth: FirebaseAuth,
    val db: FirebaseFirestore
) : SemesterRepository {

    private val semestersCollection by lazy { SemestersCollection(db) }

    override suspend fun getCurrent(): Either<Failure, Semester> {
        return try {
            val email = firebaseAuth.currentUser!!.email!!
            val col = semestersCollection.getAll(email)!!.documents.firstOrNull()
            if (col == null) {
                val semester = Semester("2020-1", 0f, 0, true)
                val data = DataSemesterUserDto("0", true, email, mutableListOf(semester.toDTO()))
                semestersCollection.create(data)
                Either.Right(semester)
            } else {
                val ob = col.toObject(DataSemesterUserDto::class.java)
                Either.Right(ob!!.semesters[0].toEntity())
            }
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }

    override suspend fun getAll(): Either<Failure, List<Semester>> {
        return try {
            val email = firebaseAuth.currentUser!!.email!!
            val col = semestersCollection.getAll(email)!!.documents.first()
                .toObject(DataSemesterUserDto::class.java)!!

            Either.Right(col.semesters.map {
                it.toEntity()
            })
        } catch (execption: Exception) {
            Either.Left(Failure.ServerError(execption))
        }
    }

    override suspend fun save(semester: Semester): Either<Failure, Semester> {
        return Either.Right(semester)
    }

    override suspend fun changeCurrentSemester(semester: Semester): Either<Failure, Semester> {
        delay(1001)
        return Either.Right(semester)
    }
}
