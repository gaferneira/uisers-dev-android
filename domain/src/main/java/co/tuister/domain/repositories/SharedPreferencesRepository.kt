package co.tuister.domain.repositories

interface SharedPreferencesRepository {
    suspend fun getString(key: String): String
    suspend fun getBoolean(key: String): Boolean
    suspend fun saveData(key: String, value: Any)
}
