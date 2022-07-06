package co.tuister.data.migration

import co.tuister.data.repositories.MyCareerRepository
import co.tuister.data.utils.BackupCollection
import co.tuister.data.utils.ConnectivityUtil
import co.tuister.data.utils.UsersCollection
import co.tuister.data.utils.await
import co.tuister.data.utils.getEmail
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.repositories.MigrationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class MigrationRepositoryImpl(
    private val gson: Gson,
    val db: FirebaseFirestore,
    val auth: FirebaseAuth,
    connectivityUtil: ConnectivityUtil
) : MyCareerRepository(auth, db, connectivityUtil), MigrationRepository {

    private val backupCollection by lazy { BackupCollection(db, connectivityUtil) }
    private val usersCollection by lazy { UsersCollection(db, connectivityUtil) }

    override
    suspend fun migrate(): Either<Failure, Boolean> {
        return try {
            // This chunk of code as been removed as we, no longer support migrations.
            val email = firebaseAuth.getEmail()
            finishProcessMigration(email)
            Either.Right(true)
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }

    private suspend fun finishProcessMigration(email: String) {
        usersCollection.getByEmail(email)?.reference?.update(
            UsersCollection.FIELD_USER_MIGRATION, true
        )?.await()

        backupCollection.deleteUserBackup(email)
    }
}
