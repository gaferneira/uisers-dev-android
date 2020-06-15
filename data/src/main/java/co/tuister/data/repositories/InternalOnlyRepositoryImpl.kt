package co.tuister.data.repositories

import android.content.Context
import co.tuister.data.await
import co.tuister.data.dto.CareerDto
import co.tuister.data.dto.CareerSubjectDto
import co.tuister.data.dto.UserDataDto
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.BaseCollection
import co.tuister.data.utils.BaseCollection.Companion.FIELD_CAREERS
import co.tuister.data.utils.BaseCollection.Companion.FIELD_SUBJECTS
import co.tuister.data.utils.UsersCollection
import co.tuister.data.utils.objectToMap
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.InternalOnlyRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class InternalOnlyRepositoryImpl(
    private val context: Context,
    private val gson: Gson,
    private val db: FirebaseFirestore
) : InternalOnlyRepository {

    private val baseCollection by lazy { BaseCollection(db) }
    private val usersCollection by lazy { UsersCollection(db) }

    override
    suspend fun loadDataCareers() {
        try {
            context.assets.open("careers.json").bufferedReader().use {
                val text = it.readText()

                val list = gson.fromJson(text, Array<CareerDto>::class.java).toList()
                    .map { subject -> subject.objectToMap() }

                baseCollection.document(BaseCollection.DOCUMENT)
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

                val list = gson.fromJson(text, Array<CareerSubjectDto>::class.java).toList()
                    .map { subject -> subject.objectToMap() }

                baseCollection.document(BaseCollection.DOCUMENT)
                    .update(mapOf(Pair(FIELD_SUBJECTS, list)))
                    .await()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override
    suspend fun getAllUserData(): Either<Failure, List<User>> {
        return try {
            val data = usersCollection.getAllUserData()!!.map {
                it.toObject(UserDataDto::class.java)!!.toEntity()
            }
            Either.Right(data)
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }
}