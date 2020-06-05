package co.tuister.data.repositories

import co.tuister.data.await
import co.tuister.data.dto.UserDataDto
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.LoginRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginRepositoryImpl(
    val firebaseAuth: FirebaseAuth,
    val firebaseFirestore: FirebaseFirestore
) : LoginRepository {


    override suspend fun login(email: String, password: String): Either<Failure, User?> {
        return try {
            val data = firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            val dataUser = firebaseFirestore
                .collection("data_users")
                .whereEqualTo("correo", email)
                .get()
                .await()
            val user = dataUser!!.documents[0].toObject(User::class.java)
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
                val us = UserDataDto(
                    user.name,
                    user.career,
                    user.email,
                    user.semester,
                    user.period,
                    user.code
                )
                firebaseFirestore.collection("data_users").add(us).await()
            }
            Either.Right(true)
        } catch (e: Exception) {
            Either.Left(Failure.ServerError(e))
        }
    }

    override suspend fun hasSessionActive(): Boolean = firebaseAuth.currentUser != null
}