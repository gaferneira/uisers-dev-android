package co.tuister.data.repositories

import android.net.Uri
import co.tuister.data.await
import co.tuister.data.dto.UserDataDto
import co.tuister.data.dto.toDTO
import co.tuister.data.dto.toEntity
import co.tuister.data.utils.COLLECTION_DATA
import co.tuister.data.utils.COLLECTION_USERS
import co.tuister.data.utils.FIELD_USER_EMAIL
import co.tuister.data.utils.FIELD_USER_FCM
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.Failure.AuthenticationError
import co.tuister.domain.entities.Career
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class UserRepositoryImpl(
    val firebaseAuth: FirebaseAuth,
    val firebaseFirestore: FirebaseFirestore,
    val firebaseStorage: FirebaseStorage
) :
    UserRepository {

    override suspend fun getUser(): Either<Failure, User> {
        val current = firebaseAuth.currentUser ?: return Either.Left(AuthenticationError())

        if (firebaseAuth.currentUser?.isEmailVerified != true) {
            return Either.Left(AuthenticationError())
        }

        val data = firebaseFirestore
            .collection(COLLECTION_USERS)
            .whereEqualTo(FIELD_USER_EMAIL, current.email)
            .get()
            .await()

        return try {
            val userDataDto: UserDataDto? = data?.let {
                it.firstOrNull()?.toObject(UserDataDto::class.java)
            }

            Either.Right(userDataDto!!.toEntity())
        } catch (exception: Exception) {
            Either.Left(Failure.DataError(exception, current.email!!))
        }
    }

    override suspend fun updateUser(user: User): Either<Failure, Boolean> {
        val current = firebaseAuth.currentUser ?: return Either.Left(AuthenticationError())

        if (firebaseAuth.currentUser?.isEmailVerified != true) {
            return Either.Left(AuthenticationError())
        }

        val data = firebaseFirestore
            .collection(COLLECTION_USERS)
            .whereEqualTo(FIELD_USER_EMAIL, current.email)
            .get()
            .await()!!.documents.firstOrNull()

        return try {
            val map = objectToMap(user.toDTO())
            firebaseFirestore.collection(COLLECTION_USERS).document(data?.id!!).update(map).await()
            Either.Right(true)
        } catch (exception: Exception) {
            Either.Left(Failure.DataError(exception, current.email!!))
        }
    }

    override suspend fun updateFCMToken(): Boolean {
        val current = firebaseAuth.currentUser ?: return false

        if (firebaseAuth.currentUser?.isEmailVerified != true) {
            return false
        }

        val data = firebaseFirestore
            .collection(COLLECTION_USERS)
            .whereEqualTo(FIELD_USER_EMAIL, current.email)
            .get()
            .await()!!.documents.firstOrNull()
        val token = FirebaseInstanceId.getInstance().instanceId.await()!!.token


        return try {
            if (data?.get(FIELD_USER_FCM) == token) {
                true
            } else {
                val map = mutableMapOf<String, String>()
                map[FIELD_USER_FCM] = token
                firebaseFirestore.collection(COLLECTION_USERS).document(data?.id!!)
                    .update(map as Map<String, Any>).await()
                true
            }
        } catch (exception: Exception) {
            false
        }
    }

    override suspend fun reSendVerifyEmail(): Boolean {
        val current = firebaseAuth.currentUser ?: return false

        return try {
            current.sendEmailVerification().await()
            true
        } catch (exception: Exception) {
            false
        }
    }

    private fun objectToMap(obj: Any): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        for (field in obj.javaClass.declaredFields) {
            field.isAccessible = true
            try {
                map[field.name] = field[obj]
            } catch (e: java.lang.Exception) {
            }
        }
        return map
    }

    override suspend fun getCareers(): Either<Failure, List<Career>> {
        val data = firebaseFirestore
            .collection(COLLECTION_DATA)
            .document("uis")
            .get()
            .await()

        return try {
            val result = data?.get("careers") as MutableList<HashMap<String, String>>
            val list = mutableListOf<Career>()
            for (res in result) {
                list.add(Career(res.get("codigo")!!, res.get("nombre")!!))
            }
            Either.Right(list)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    override suspend fun getCampus(): Either<Failure, List<String>> {
        val data = firebaseFirestore
            .collection(COLLECTION_DATA)
            .document("uis")
            .get()
            .await()

        return try {
            val result = data?.get("sedes") as MutableList<String>
            Either.Right(result)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    override suspend fun downloadImage(email: String): Either<Failure, Uri> {
        return try {
            val url = firebaseStorage.reference.child("$email/profile.jpg").downloadUrl.await()
            Either.Right(url!!)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }
}