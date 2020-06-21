package co.tuister.data.repositories

import co.tuister.data.dto.SchedulePeriodDto
import co.tuister.data.dto.toDTO
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.SemestersCollection
import co.tuister.data.utils.await
import co.tuister.data.utils.objectToMap
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.domain.repositories.ScheduleRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ScheduleRepositoryImpl(
    firebaseAuth: FirebaseAuth,
    db: FirebaseFirestore
) : MyCareerRepository(firebaseAuth, db), ScheduleRepository {

    override suspend fun save(period: SchedulePeriod): SchedulePeriod {
        if (period.id.isNotEmpty()) {
            val subjectDto = semestersCollection.documentByPath(period.id).get().await()!!
            subjectDto.reference.update(period.toDTO().objectToMap())
        } else {
            val id = semestersCollection.documentByPath(getCurrentSemesterPath())
                .collection(SemestersCollection.COL_SCHEDULE)
                .add(period.toDTO())
                .await()!!.path
            period.id = id
        }

        return period
    }

    override suspend fun getSchedule(): List<SchedulePeriod> {
        val periods = semestersCollection.documentByPath(getCurrentSemesterPath())
            .collection(SemestersCollection.COL_SCHEDULE)
            .get()
            .await()!!
            .documents.map {
                val path = it.reference.path
                it.toObject(SchedulePeriodDto::class.java)!!.toEntity(path)
            }
        return periods.sortedBy { (it.day - 2) % 8 } // First day monday
    }

    override suspend fun remove(item: SchedulePeriod): Boolean {
        return if (item.id.isNotEmpty()) {
            semestersCollection.documentByPath(item.id).delete().await()
            true
        } else {
            false
        }
    }

}
