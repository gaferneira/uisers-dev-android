package co.tuister.data.repositories

import android.net.Uri
import co.tuister.data.dto.toDTO
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.data.utils.UsersCollection
import co.tuister.data.utils.await
import co.tuister.data.utils.isEmailVerified
import co.tuister.data.utils.translateFirebaseException
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.Failure.EmailNotVerifiedError
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LoginRepositoryImpl(
    firebaseAuth: FirebaseAuth,
    val db: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    connectivityUtil: ConnectivityUtil
) : MyCareerRepository(firebaseAuth, db, connectivityUtil), LoginRepository {

    private val usersCollection by lazy { UsersCollection(db, connectivityUtil) }

    override suspend fun login(email: String, password: String): Either<Failure, User> {
        return try {
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            if (!firebaseAuth.isEmailVerified()) {
                return Either.Left(EmailNotVerifiedError())
            }

            val user = usersCollection.getByEmail(email)?.toObject(User::class.java)

            // check if current semester exists
            getCurrentSemesterPath()

            Either.Right(user!!)
        } catch (e: Exception) {
            Either.Left(e.translateFirebaseException())
        }
    }

    override suspend fun recoverPassword(email: String): Either<Failure, Boolean> {
        return try {
            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()
            Either.Right(true)
        } catch (e: FirebaseAuthInvalidUserException) {
            Either.Right(false)
        } catch (e: Exception) {
            Either.Left(e.translateFirebaseException())
        }
    }

    override suspend fun logout(): Either<Failure, Boolean> {
        return eitherResult {
            firebaseAuth.signOut()
            true
        }
    }

    override suspend fun register(user: User, password: String): Either<Failure, Boolean> {
        return eitherResult {
            val data = firebaseAuth
                .createUserWithEmailAndPassword(user.email, password)
                .await()
            data?.user?.let {
                it.sendEmailVerification().await()
                usersCollection.collection().add(user.toDTO()).await()
            }
            true
        }
    }

    override suspend fun hasSessionActive(): Either<Failure, Boolean> {
        return eitherResult {
            firebaseAuth.currentUser != null
        }
    }

    override suspend fun uploadImage(uri: Uri, email: String): Either<Failure, Boolean> {
        val profileRef = firebaseStorage.reference.child("$email/profile.jpg")
        return eitherResult {
            profileRef.putFile(uri).await()
            true
        }
    }
}
