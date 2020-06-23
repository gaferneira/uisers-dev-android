package co.tuister.data.repositories

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import co.tuister.domain.repositories.SharedPreferencesRepository

class SharePreferencesRepositoryImpl(applicationContext: Context) :
    SharedPreferencesRepository {
    companion object {
        private const val UISERS_PREF_NAME = "UISERS_PREF_NAME"
        const val KEY_FIRST_TIME = "KEY_FIRST_TIME"
    }

    private val sharedPref: SharedPreferences =
        applicationContext.getSharedPreferences(UISERS_PREF_NAME, MODE_PRIVATE)

    override suspend fun getString(key: String): String {
        return sharedPref.getString(key, "")!!
    }

    override suspend fun getBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, true)
    }

    override suspend fun saveData(key: String, value: Any) {
        when (value) {
            is Boolean -> sharedPref.edit().putBoolean(key, value).apply()
            is String -> sharedPref.edit().putString(key, value).apply()
        }
    }
}
