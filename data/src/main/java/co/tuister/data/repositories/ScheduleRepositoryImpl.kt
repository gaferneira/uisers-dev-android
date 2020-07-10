package co.tuister.data.repositories

import co.tuister.data.dto.SchedulePeriodDto
import co.tuister.data.dto.toDTO
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.data.utils.SemestersCollection
import co.tuister.data.utils.await
import co.tuister.data.utils.getSource
import co.tuister.data.utils.objectToMap
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.repositories.ScheduleRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class ScheduleRepositoryImpl(
    firebaseAuth: FirebaseAuth,
    db: FirebaseFirestore,
    private val connectivityUtil: ConnectivityUtil
) : MyCareerRepository(firebaseAuth, db, connectivityUtil), ScheduleRepository {

    override suspend fun save(period: SchedulePeriod): Either<Failure, SchedulePeriod> {
        return eitherResult {
            if (period.id.isNotEmpty()) {
                val subjectDto = semestersCollection.documentByPath(period.id).get(connectivityUtil.getSource()).await()!!
                subjectDto.reference.update(period.toDTO().objectToMap())
            } else {
                val id = semestersCollection.documentByPath(getCurrentSemesterPath())
                    .collection(SemestersCollection.COL_SCHEDULE)
                    .add(period.toDTO())
                    .await()!!.path
                period.id = id
            }

            period
        }
    }

    override suspend fun getSchedule(): Either<Failure, List<SchedulePeriod>> {
        return eitherResult {
            val periods = semestersCollection.documentByPath(getCurrentSemesterPath())
                .collection(SemestersCollection.COL_SCHEDULE)
                .get(connectivityUtil.getSource())
                .await()!!
                .documents.map {
                    val path = it.reference.path
                    it.toObject(SchedulePeriodDto::class.java)!!.toEntity(path)
                }
            periods.sortedBy { (it.day - Calendar.MONDAY) % DAYS_WEEK } // First day monday
        }
    }

    override suspend fun remove(item: SchedulePeriod): Either<Failure, Boolean> {
        return eitherResult {
            if (item.id.isNotEmpty()) {
                semestersCollection.documentByPath(item.id).delete().await()
                true
            } else {
                false
            }
        }
    }

    companion object {
        private const val DAYS_WEEK = 7
    }
}
