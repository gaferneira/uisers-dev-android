package co.tuister.domain.repositories

import co.tuister.domain.entities.Semester

interface SemesterRepository {
    suspend fun getCurrent(): Semester
    suspend fun getAll(): List<Semester>
    suspend fun save(semester: Semester): Semester
    suspend fun changeCurrentSemester(semester: Semester): Semester
    suspend fun remove(item: Semester): Boolean
}
