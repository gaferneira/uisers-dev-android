package co.tuister.data.repositories

import co.tuister.data.await
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
import kotlinx.coroutines.delay
import java.util.*

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


    override suspend fun getScheduleByDate(date: Date): Either<Failure, List<SchedulePeriod>> {
        delay(1000)
        return Either.Right(
            listOf(
                SchedulePeriod("", "Calculo I",  1,"08:00", "10:00", "CT 301"),
                SchedulePeriod("", "Quimica",1, "10:00", "12:00", "LP 310")
            )
        )
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
            Either.Right(periods)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

}