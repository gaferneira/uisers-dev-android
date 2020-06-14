package co.tuister.data.repositories

import android.net.Uri
import co.tuister.data.await
import co.tuister.data.dto.UserDataDto
import co.tuister.data.dto.toDTO
import co.tuister.data.dto.toEntity
import co.tuister.data.dto.*
import co.tuister.data.utils.*
import co.tuister.data.utils.BaseCollection.Companion.FIELD_CAMPUS
import co.tuister.data.utils.BaseCollection.Companion.FIELD_CAREERS
import co.tuister.data.utils.UsersCollection.Companion.FIELD_USER_EMAIL
import co.tuister.data.utils.UsersCollection.Companion.FIELD_USER_FCM
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

class UserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) :
    UserRepository {

    private val usersCollection by lazy { UsersCollection(db) }
    private val baseCollection by lazy { BaseCollection(db) }

    override suspend fun getUser(): Either<Failure, User> {
        val current = firebaseAuth.currentUser ?: return Either.Left(AuthenticationError())

        if (firebaseAuth.currentUser?.isEmailVerified != true) {
            return Either.Left(AuthenticationError())
        }

        val data = usersCollection.collection()
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

        val data = usersCollection.collection()
            .whereEqualTo(FIELD_USER_EMAIL, current.email)
            .get()
            .await()!!.documents.firstOrNull()

        return try {
            val map = user.toDTO().objectToMap()
            usersCollection.collection().document(data?.id!!).update(map).await()
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

        val data = usersCollection.collection()
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
                usersCollection.collection().document(data?.id!!)
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

    override suspend fun getCareers(): Either<Failure, List<Career>> {

        return try {
            val data = baseCollection.getBaseDocument()

            val result: List<HashMap<String, String>> =
                data?.get(FIELD_CAREERS)?.castToList() ?: listOf()
            val list = result.map {
                Career(it["id"]!!, it["name"]!!)
            }
            Either.Right(list)
        } catch (exception: Exception) {
            Either.Left(Failure.ServerError(exception))
        }
    }

    override suspend fun getCampus(): Either<Failure, List<String>> {
        return try {
            val data = baseCollection.getBaseDocument()
            val result = data?.get(FIELD_CAMPUS).castToList<String>() ?: listOf()
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