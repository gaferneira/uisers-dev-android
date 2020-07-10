package co.tuister.data.repositories

import android.net.Uri
import co.tuister.data.dto.UserDataDto
import co.tuister.data.dto.toDTO
import co.tuister.data.dto.toEntity
import co.tuister.data.repositories.SharePreferencesRepositoryImpl.Companion.KEY_FIRST_TIME
import co.tuister.data.utils.BaseCollection
import co.tuister.data.utils.BaseCollection.Companion.FIELD_CAMPUS
import co.tuister.data.utils.BaseCollection.Companion.FIELD_CAREERS
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.data.utils.FeedbackCollection
import co.tuister.data.utils.FeedbackCollection.Companion.FIELD_FEEDBACK
import co.tuister.data.utils.FeedbackCollection.Companion.FIELD_FEEDBACK_DATE
import co.tuister.data.utils.FeedbackCollection.Companion.FIELD_FEEDBACK_EMAIL
import co.tuister.data.utils.UsersCollection
import co.tuister.data.utils.UsersCollection.Companion.FIELD_USER_EMAIL
import co.tuister.data.utils.UsersCollection.Companion.FIELD_USER_FCM
import co.tuister.data.utils.await
import co.tuister.data.utils.castToList
import co.tuister.data.utils.getSource
import co.tuister.data.utils.isEmailVerified
import co.tuister.data.utils.objectToMap
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.Failure.AuthenticationError
import co.tuister.domain.entities.Career
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.SharedPreferencesRepository
import co.tuister.domain.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import java.net.HttpURLConnection
import java.util.Date

class UserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val connectivityUtil: ConnectivityUtil
) : BaseRepositoryImpl(),
    UserRepository {

    private val usersCollection by lazy { UsersCollection(db, connectivityUtil) }
    private val baseCollection by lazy { BaseCollection(db, connectivityUtil) }
    private val feedbackCollection by lazy { FeedbackCollection(db) }

    override suspend fun getUser(): Either<Failure, User> {
        val current = firebaseAuth.currentUser ?: return Either.Left(AuthenticationError())

        if (!firebaseAuth.isEmailVerified()) {
            return Either.Left(
                Failure.EmailNotVerifiedError(
                    Exception(
                        firebaseAuth.currentUser?.email ?: ""
                    )
                )
            )
        }

        val data = usersCollection.collection()
            .whereEqualTo(FIELD_USER_EMAIL, current.email)
            .get(connectivityUtil.getSource())
            .await()

        val userDataDto: UserDataDto? = data?.let {
            it.firstOrNull()?.toObject(UserDataDto::class.java)
        }

        return Either.Right(userDataDto!!.toEntity())
    }

    override suspend fun updateUser(user: User): Either<Failure, Boolean> {
        val current = firebaseAuth.currentUser ?: return Either.Left(AuthenticationError())

        if (!firebaseAuth.isEmailVerified()) {
            return Either.Left(AuthenticationError())
        }

        val data = usersCollection.collection()
            .whereEqualTo(FIELD_USER_EMAIL, current.email)
            .get(connectivityUtil.getSource())
            .await()!!.documents.firstOrNull()

        val map = user.toDTO().objectToMap()
        usersCollection.collection().document(data?.id!!).update(map).await()
        return Either.Right(true)
    }

    override suspend fun updateFCMToken(): Either<Failure, Boolean> {
        return eitherResult {
            val current = firebaseAuth.currentUser ?: return@eitherResult false

            if (!firebaseAuth.isEmailVerified()) {
                return@eitherResult false
            }

            val data = usersCollection.collection()
                .whereEqualTo(FIELD_USER_EMAIL, current.email)
                .get(connectivityUtil.getSource())
                .await()!!.documents.firstOrNull()
            val token = FirebaseInstanceId.getInstance().instanceId.await()!!.token

            if (data?.get(FIELD_USER_FCM) == token) {
                true
            } else {
                val map = mutableMapOf<String, String>()
                map[FIELD_USER_FCM] = token
                usersCollection.collection().document(data?.id!!)
                    .update(map as Map<String, Any>).await()
                true
            }
        }
    }

    override suspend fun reSendVerifyEmail(): Either<Failure, Boolean> {
        return eitherResult {
            val current = firebaseAuth.currentUser ?: return@eitherResult false
            current.sendEmailVerification().await()
            true
        }
    }

    override suspend fun sendFeedback(comment: String): Either<Failure, Boolean> {
        return eitherResult {
            val feedback = hashMapOf(
                FIELD_FEEDBACK to comment,
                FIELD_FEEDBACK_EMAIL to firebaseAuth.currentUser?.email,
                FIELD_FEEDBACK_DATE to Date()
            )
            feedbackCollection.collection().add(feedback).await()
            true
        }
    }

    override suspend fun checkFirstTime(): Boolean =
        sharedPreferencesRepository.getBoolean(KEY_FIRST_TIME)

    override suspend fun disableFirstTime(): Boolean {
        sharedPreferencesRepository.saveData(KEY_FIRST_TIME, false)
        return true
    }

    override suspend fun getCareers(): Either<Failure, List<Career>> {
        return eitherResult {
            val data = baseCollection.getBaseDocument()

            val result: List<HashMap<String, String>> =
                data?.get(FIELD_CAREERS)?.castToList() ?: listOf()
            result.map {
                Career(it["id"]!!, it["name"]!!)
            }
        }
    }

    override suspend fun getCampus(): Either<Failure, List<String>> {
        return eitherResult {
            val data = baseCollection.getBaseDocument()
            data?.get(FIELD_CAMPUS).castToList() ?: listOf<String>()
        }
    }

    override suspend fun downloadImage(email: String): Either<Failure, Uri> {
        return try {
            val url = firebaseStorage.reference.child("$email/profile.jpg").downloadUrl.await()
            if (url != null) {
                Either.Right(url)
            } else {
                Either.Left(Failure.DataNotFound())
            }
        } catch (exception: StorageException) {
            if (exception.httpResultCode == HttpURLConnection.HTTP_NOT_FOUND) {
                Either.Left(Failure.DataNotFound())
            } else {
                Either.Left(Failure.analyzeException(exception))
            }
        }
    }
}
