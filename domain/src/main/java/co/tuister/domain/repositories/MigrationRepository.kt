package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure

interface MigrationRepository {
    suspend fun migrate(): Either<Failure, Boolean>
}
