package co.tuister.domain.usecases

import android.util.Log
import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.repositories.MigrationRepository
import kotlin.system.measureTimeMillis

class MigrationUseCase(
    private val migrationRepository: MigrationRepository
) : NoParamsUseCase<Boolean>() {
    override suspend fun run(): Either<Failure, Boolean> {
        var result : Either<Failure, Boolean> = Either.Left(Failure.ServerError())
        val checkTime = measureTimeMillis {
            result = migrationRepository.migrate()
        }
        Log.d("checkTime", checkTime.toString())
        return result
    }
}