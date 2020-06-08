package co.tuister.data.repositories

import android.net.Uri
import co.tuister.data.await
import co.tuister.data.dto.UserDataDto
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.Failure.AuthenticationError
import co.tuister.domain.entities.Career
import co.tuister.domain.entities.User
import co.tuister.domain.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    override suspend fun getCareers(): Either<Failure, List<Career>> {
        val data = firebaseFirestore
            .collection("data_base")
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
            .collection("data_base")
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