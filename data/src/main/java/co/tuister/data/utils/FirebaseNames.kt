package co.tuister.data.utils

import com.google.firebase.firestore.FirebaseFirestore

sealed class FirebaseCollection(private val db: FirebaseFirestore, val name: String) {
    fun collection() = db.collection(name)
    fun document(name: String) = collection().document(name)
    fun documentByPath(path: String) = db.document(path)
}

class BaseCollection(db: FirebaseFirestore, private val connectivityUtil: ConnectivityUtil) : FirebaseCollection(db, NAME) {
    suspend fun getBaseDocument() = document(DOCUMENT).get(connectivityUtil.getSource()).await()
    suspend fun getMapDocument() = document(DOCUMENT_MAP).get(connectivityUtil.getSource()).await()
    suspend fun getCalendarDocument() = document(DOCUMENT_CALENDAR).get(connectivityUtil.getSource()).await()

    companion object {
        const val NAME = "data_base"
        const val DOCUMENT = "uis"
        const val DOCUMENT_MAP = "map"
        const val DOCUMENT_CALENDAR = "calendar"
        const val FIELD_SUBJECTS = "subjects"
        const val FIELD_CAREERS = "careers"
        const val FIELD_CAMPUS = "sedes"
        const val FIELD_MAP_PLACES = "map_places"
        const val FIELD_MAP_SITES = "map_sites"
        const val FIELD_CALENDAR = "academico"
    }
}

class UsersCollection(db: FirebaseFirestore, private val connectivityUtil: ConnectivityUtil) : FirebaseCollection(db, NAME) {
    suspend fun getByEmail(email: String) =
        collection()
            .whereEqualTo(FIELD_USER_EMAIL, email)
            .get(connectivityUtil.getSource())
            .await()
            ?.documents?.firstOrNull()

    suspend fun getAllUserData() = collection().get(connectivityUtil.getSource()).await()?.documents

    companion object {
        const val NAME = "data_users"
        const val FIELD_USER_FCM = "fcmId"
        const val FIELD_USER_EMAIL = "correo"
        const val FIELD_USER_MIGRATION = "migration"
    }
}

class SemestersCollection(db: FirebaseFirestore) : FirebaseCollection(db, NAME) {

    companion object {
        const val NAME = "semesters"
        const val COL_SEMESTERS = "semesters"
        const val COL_SUBJECTS = "subjects"
        const val COL_SCHEDULE = "schedule"
        const val FIELD_EMAIL = "email"
        const val FIELD_CURRENT_SEMESTER = "currentSemester"
        const val FIELD_PERIOD = "period"
        const val FIELD_SUBJECT_CODE = "code"
        const val COL_NOTES = "notes"
    }
}

class TaskManagerCollection(db: FirebaseFirestore) : FirebaseCollection(db, NAME) {
    companion object {
        const val NAME = "task_manager"
        const val FIELD_EMAIL = "email"
        const val COL_TASKS = "tasks"
        const val DUE_DATE = "due_date"
    }
}

class BackupCollection(db: FirebaseFirestore, private val connectivityUtil: ConnectivityUtil) : FirebaseCollection(db, NAME) {
    suspend fun getUserBackup(email: String) = document(email).get(connectivityUtil.getSource()).await()?.getString(FIELD_DATA)
    suspend fun deleteUserBackup(email: String) = document(email).delete().await()

    companion object {
        const val NAME = "backup"
        const val FIELD_DATA = "data"
    }
}

class FeedbackCollection(db: FirebaseFirestore) : FirebaseCollection(db, NAME) {
    companion object {
        const val NAME = "feedback"
        const val FIELD_FEEDBACK = "comment"
        const val FIELD_FEEDBACK_EMAIL = "email"
        const val FIELD_FEEDBACK_DATE = "date"
    }
}

class FeedCollection(db: FirebaseFirestore) : FirebaseCollection(db, NAME) {
    companion object {
        const val NAME = "feed"
    }
}
