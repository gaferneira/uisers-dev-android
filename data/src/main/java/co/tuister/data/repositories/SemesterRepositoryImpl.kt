package co.tuister.data.repositories

import co.tuister.data.dto.SemesterUserDto
import co.tuister.data.dto.toDTO
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.data.utils.SemestersCollection
import co.tuister.data.utils.await
import co.tuister.data.utils.getSource
import co.tuister.data.utils.objectToMap
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Semester
import co.tuister.domain.repositories.SemesterRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class SemesterRepositoryImpl(
    firebaseAuth: FirebaseAuth,
    db: FirebaseFirestore,
    private val connectivityUtil: ConnectivityUtil
) : MyCareerRepository(firebaseAuth, db, connectivityUtil), SemesterRepository {

    override suspend fun getCurrent(): Either<Failure, Semester> {
        return eitherResult {
            val semesterDocument = getCurrentSemester()
            val semester = semesterDocument.toObject(SemesterUserDto::class.java)!!
            semester.toEntity(semesterDocument.reference.path)
        }
    }

    override suspend fun getAll(): Either<Failure, List<Semester>> {
        return eitherResult {
            val currentSemester = getCurrentSemester().toObject(SemesterUserDto::class.java)!!
            val collection = getSemestersCollection()
                .get(connectivityUtil.getSource())
                .await()!!

            val list = collection.documents.map {
                val path = it.reference.path
                it.toObject(SemesterUserDto::class.java)!!.toEntity(path).apply {
                    current = period == currentSemester.period
                }
            }

            list.sortedByDescending { it.period }
        }
    }

    override suspend fun save(semester: Semester): Either<Failure, Semester> {
        return eitherResult {
            if (semester.id.isNotEmpty()) {
                val subjectDto = semestersCollection.documentByPath(semester.id).get(connectivityUtil.getSource()).await()!!
                subjectDto.reference.update(semester.toDTO().objectToMap())
            } else {
                val id = getSemestersCollection()
                    .add(semester.toDTO())
                    .await()!!.path
                semester.id = id
            }

            if (semester.current) {
                getUserDocument()
                    .update(SemestersCollection.FIELD_CURRENT_SEMESTER, semester.period)
                    .await()
            }

            semester
        }
    }

    override suspend fun changeCurrentSemester(semester: Semester): Either<Failure, Semester> {
        return eitherResult {
            getUserDocument()
                .update(SemestersCollection.FIELD_CURRENT_SEMESTER, semester.period)
                .await()

            semester
        }
    }

    override suspend fun remove(item: Semester): Either<Failure, Boolean> {
        return eitherResult {
            if (item.id.isNotEmpty()) {
                semestersCollection.documentByPath(item.id).delete().await()
                true
            } else {
                false
            }
        }
    }

    private suspend fun getCurrentSemester(): DocumentSnapshot {
        return semestersCollection.documentByPath(getCurrentSemesterPath())
            .get(connectivityUtil.getSource())
            .await()!!
    }
}
