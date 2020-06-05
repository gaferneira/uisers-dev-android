package co.tuister.data.repositories

import co.tuister.data.await
import co.tuister.data.dto.UserDataDto
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepositoryImpl(val firebaseAuth: FirebaseAuth, val firebaseFirestore: FirebaseFirestore) :
    UserRepository {

    override suspend fun getUser(): Either<Failure, User> {
        val current = firebaseAuth.currentUser ?: return Either.Left(Failure.AuthenticationError())

        if (firebaseAuth.currentUser?.isEmailVerified == true) {
            return Either.Left(Failure.AuthenticationError())
        }

        val data = firebaseFirestore
            .collection("data_users")
            .whereEqualTo("correo", current.email)
            .get()
            .await()

        val userDataDto: UserDataDto? = data?.let {
            it.firstOrNull()?.toObject(UserDataDto::class.java)
        }

        return try {
            val user = User(
                userDataDto!!.nombre,
                current.email!!,
                userDataDto!!.carrera,
                userDataDto.ingreso + "-" + userDataDto.periodo
            )
            Either.Right(user)
        } catch (exception: Exception) {
            Either.Left(Failure.DataError(exception, current.email!!))
        }
    }
}