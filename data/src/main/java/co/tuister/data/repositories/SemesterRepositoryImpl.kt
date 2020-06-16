package co.tuister.data.repositories

import co.tuister.data.utils.await
import co.tuister.data.dto.SemesterUserDto
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
    firebaseAuth: FirebaseAuth,
    db: FirebaseFirestore
) : MyCareerRepository(firebaseAuth, db), SemesterRepository {

    override suspend fun getCurrent(): Either<Failure, Semester> {
        return try {
            val semester = getCurrentSemester()
            Either.Right(semester.toEntity())
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }

    override suspend fun getAll(): Either<Failure, List<Semester>> {
        return try {

            val currentSemester = getCurrentSemester()
            val collection = getSemestersCollection()
                .get()
                .await()!!

            val list = collection.documents.map {
                it.toObject(SemesterUserDto::class.java)!!.toEntity().apply {
                    current = period == currentSemester.period
                }
            }

            Either.Right(list.sortedByDescending { it.period })
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    override suspend fun save(semester: Semester): Either<Failure, Semester> {
        return try {
            getSemestersCollection()
                .add(semester.toDTO())
                .await()

            if (semester.current) {
                getUserDocument()
                    .update(SemestersCollection.FIELD_CURRENT_SEMESTER, semester.period)
                    .await()
            }
            Either.Right(semester)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    override suspend fun changeCurrentSemester(semester: Semester): Either<Failure, Semester> {
        return try {
            getUserDocument()
                .update(SemestersCollection.FIELD_CURRENT_SEMESTER, semester.period)
                .await()

            Either.Right(semester)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    private suspend fun getCurrentSemester(): SemesterUserDto {
        val document = semestersCollection.documentByPath(getCurrentSemesterPath())
            .get()
            .await()!!

        return document.toObject(SemesterUserDto::class.java)!!

    }
}