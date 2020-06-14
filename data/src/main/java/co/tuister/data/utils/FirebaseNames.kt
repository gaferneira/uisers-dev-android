package co.tuister.data.utils

import co.tuister.data.await
import co.tuister.data.dto.DataSemesterUserDto
import com.google.firebase.firestore.FirebaseFirestore

sealed class FirebaseCollection(private val db: FirebaseFirestore, val name: String) {
    fun getCollection() = db.collection(name)
    fun getDocument(document: String) = getCollection().document(document)
}

class BaseCollection(db: FirebaseFirestore) : FirebaseCollection(db, NAME) {
    suspend fun getBaseDocument() = getDocument(DOCUMENT).get().await()

    companion object {
        const val NAME = "data_base"
        const val DOCUMENT = "uis"
        const val FIELD_SUBJECTS = "subjects"
        const val FIELD_CAREERS = "careers"
        const val FIELD_CAMPUS = "sedes"
    }
}

class UsersCollection(db: FirebaseFirestore) : FirebaseCollection(db, NAME) {
    companion object {
        const val NAME = "data_users"
        const val FIELD_USER_FCM = "fcmId"
        const val FIELD_USER_EMAIL = "correo"
    }
}

class SemestersCollection(db: FirebaseFirestore) : FirebaseCollection(db, NAME) {
    suspend fun getAll(email: String) =
        getCollection().whereEqualTo(FIELD_EMAIL, email).get().await()

    suspend fun update(data: DataSemesterUserDto, docId: String) =
        getCollection().document(docId).update(data.objectToMap()).await()

    suspend fun create(data: DataSemesterUserDto) =
        getCollection().add(data).await()

    companion object {
        const val NAME = "semesters"
        const val FIELD_EMAIL = "email"
    }
}