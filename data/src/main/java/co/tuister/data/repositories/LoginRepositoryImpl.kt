package co.tuister.data.repositories

import android.net.Uri
import co.tuister.data.await
import co.tuister.data.dto.toDTO
import co.tuister.data.utils.UsersCollection
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.Failure.EmailNotVerifiedError
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class LoginRepositoryImpl(
    val firebaseAuth: FirebaseAuth,
    val db: FirebaseFirestore,
    val firebaseStorage: FirebaseStorage
) : LoginRepository {

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
            Either.Right(user)

        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }

    override suspend fun recoverPassword(email: String): Boolean {
        return try {
            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()
            true
        } catch (e: Exception) {
            false
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
        } catch (e: Exception) {
            if (e is FirebaseAuthWeakPasswordException){
                Either.Left(Failure.AuthWeakPasswordException(e))
            } else {
                Either.Left(Failure.ServerError(e))
            }
        }
    }

    override suspend fun hasSessionActive(): Boolean = firebaseAuth.currentUser != null

    override suspend fun uploadImage(uri: Uri, email: String): Either<Failure, Boolean> {
        val profileRef = firebaseStorage.reference.child("$email/profile.jpg")
        return try {
            profileRef.putFile(uri).await()
            Either.Right(true)
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }
}