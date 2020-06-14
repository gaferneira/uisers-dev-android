package co.tuister.data.repositories

import co.tuister.data.await
import co.tuister.data.dto.DataSemesterUserDto
import co.tuister.data.dto.SemesterUserDto
import co.tuister.data.dto.toDTO
import co.tuister.data.utils.SemestersCollection
import co.tuister.domain.entities.Semester
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

abstract class MyCareerRepository(
    protected val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    protected val semestersCollection by lazy { SemestersCollection(db) }

    private var userDocumentId: String? = null
    private var email: String = ""

    protected suspend fun getUserDocument() = semestersCollection.document(getUserDocumentsId())

    protected suspend fun getSemestersCollection() = getUserDocument()
        .collection(SemestersCollection.COL_SEMESTERS)

    protected suspend fun getCurrentSemesterPath(): String {

        val doc = getUserDocument().get().await()
        val currentSemester = doc!!.toObject(DataSemesterUserDto::class.java)!!.currentSemester
        val collection = getSemestersCollection()
            .whereEqualTo(SemestersCollection.FIELD_PERIOD, currentSemester)
            .get()
            .await()

        return collection!!.documents.first().reference.path

    }

    private suspend fun getUserDocumentsId(): String {
        if (userDocumentId.isNullOrEmpty() || email != firebaseAuth.currentUser!!.email!!) {
            email = firebaseAuth.currentUser!!.email!!
            val id = semestersCollection.collection()
                .whereEqualTo(SemestersCollection.FIELD_EMAIL, email)
                .get()
                .await()!!
                .documents.firstOrNull()?.id

            userDocumentId = if (id == null) {
                val data = DataSemesterUserDto(0f, email, "2021-1")
                createInitialDocument(data)
            } else {
                id
            }
        }
        return userDocumentId!!
    }

    private suspend fun createInitialDocument(data: DataSemesterUserDto): String {
        return try {
            val semester = Semester(data.currentSemester, 0f, 0, true)
            val id = semestersCollection.collection().add(data).await()!!.id
            semestersCollection.document(id)
                .collection(SemestersCollection.COL_SEMESTERS)
                .add(semester.toDTO())
                .await()
            id
        } catch (exception: Exception) {
            exception.printStackTrace()
            ""
        }
    }

}