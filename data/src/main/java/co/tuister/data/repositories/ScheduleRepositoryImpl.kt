package co.tuister.data.repositories

import co.tuister.data.utils.await
import co.tuister.data.dto.SchedulePeriodDto
import co.tuister.data.dto.toDTO
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.SemestersCollection
import co.tuister.data.utils.objectToMap
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.repositories.ScheduleRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ScheduleRepositoryImpl(
    firebaseAuth: FirebaseAuth,
    db: FirebaseFirestore
) : MyCareerRepository(firebaseAuth, db), ScheduleRepository {

    override suspend fun save(period: SchedulePeriod): Either<Failure, SchedulePeriod> {
        return try {
            if (period.id.isNotEmpty()){
                val subjectDto = semestersCollection.documentByPath(period.id).get().await()!!
                subjectDto.reference.update(period.toDTO().objectToMap())
            } else {
                val id = semestersCollection.documentByPath(getCurrentSemesterPath())
                    .collection(SemestersCollection.COL_SCHEDULE)
                    .add(period.toDTO())
                    .await()!!.path
                period.id = id
            }

            Either.Right(period)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    override suspend fun getSchedule(): Either<Failure, List<SchedulePeriod>> {
        return try {
            val periods = semestersCollection.documentByPath(getCurrentSemesterPath())
                .collection(SemestersCollection.COL_SCHEDULE)
                .get()
                .await()!!
                .documents.map {
                    val path = it.reference.path
                    it.toObject(SchedulePeriodDto::class.java)!!.toEntity().apply {
                        id = path
                    }
                }
            Either.Right(periods.sortedBy { (it.day - 2) % 8 }) // First day monday
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

}