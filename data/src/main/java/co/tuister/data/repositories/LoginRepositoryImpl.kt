package co.tuister.data.repositories

import android.net.Uri
import co.tuister.data.dto.toDTO
import co.tuister.data.utils.UsersCollection
import co.tuister.data.utils.await
import co.tuister.data.utils.translateFirebaseException
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.Failure.EmailNotVerifiedError
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LoginRepositoryImpl(
    firebaseAuth: FirebaseAuth,
    val db: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage
) : MyCareerRepository(firebaseAuth, db), LoginRepository {

    private val usersCollection by lazy { UsersCollection(db) }

    override suspend fun login(email: String, password: String): Either<Failure, User?> {
        return try {
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            if (firebaseAuth.currentUser?.isEmailVerified != true) {
                return Either.Left(EmailNotVerifiedError())
            }

            val user = usersCollection.getByEmail(email)?.toObject(User::class.java)

            // check if current semester exists
            getCurrentSemesterPath()

            Either.Right(user)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Either.Left(Failure.AuthenticationError(e))
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e.translateFirebaseException()))
        }
    }

    override suspend fun recoverPassword(email: String): Boolean {
        return try {
            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()
            true
        } catch (e: FirebaseAuthInvalidUserException) {
            false
        } catch (e: Exception) {
            throw(e.translateFirebaseException())
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun register(user: User, password: String): Either<Failure, Boolean> {
        return try {
            val data = firebaseAuth
                .createUserWithEmailAndPassword(user.email, password)
                .await()
            data?.user?.let {
                it.sendEmailVerification().await()
                usersCollection.collection().add(user.toDTO()).await()
            }
            Either.Right(true)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Either.Left(Failure.AuthWeakPasswordException(e))
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e.translateFirebaseException()))
        }
    }

    override suspend fun hasSessionActive(): Boolean = firebaseAuth.currentUser != null

    override suspend fun uploadImage(uri: Uri, email: String): Either<Failure, Boolean> {
        val profileRef = firebaseStorage.reference.child("$email/profile.jpg")
        return try {
            profileRef.putFile(uri).await()
            Either.Right(true)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e.translateFirebaseException()))
        }
    }
}
