package co.tuister.domain.repositories

interface InternalOnlyRepository {
    suspend fun loadDataCareers()
    suspend fun loadDataSubjects()
}