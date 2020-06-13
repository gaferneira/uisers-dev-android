package co.tuister.data.repositories

import android.content.Context
import co.tuister.data.await
import co.tuister.data.dto.CareerDto
import co.tuister.data.dto.SubjectDto
import co.tuister.data.utils.COLLECTION_DATA
import co.tuister.data.utils.FIELD_CAREERS
import co.tuister.data.utils.FIELD_SUBJECTS
import co.tuister.data.utils.objectToMap
import co.tuister.domain.repositories.InternalOnlyRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class InternalOnlyRepositoryImpl(
    private val context: Context,
    private val firebaseFirestore: FirebaseFirestore
) : InternalOnlyRepository {

    override
    suspend fun loadDataCareers() {
        try {
            context.assets.open("careers.json").bufferedReader().use {
                val text = it.readText()
                val gson = Gson()

                val list = gson.fromJson(text, Array<CareerDto>::class.java).toList()
                    .map { subject -> subject.objectToMap() }

                firebaseFirestore
                    .collection(COLLECTION_DATA)
                    .document("uis")
                    .update(mapOf(Pair(FIELD_CAREERS, list)))
                    .await()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override
    suspend fun loadDataSubjects() {
        try {
            context.assets.open("subjects.json").bufferedReader().use {
                val text = it.readText()
                val gson = Gson()

                val list = gson.fromJson(text, Array<SubjectDto>::class.java).toList()
                    .map { subject -> subject.objectToMap() }

                firebaseFirestore
                    .collection(COLLECTION_DATA)
                    .document("uis")
                    .update(mapOf(Pair(FIELD_SUBJECTS, list)))
                    .await()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}