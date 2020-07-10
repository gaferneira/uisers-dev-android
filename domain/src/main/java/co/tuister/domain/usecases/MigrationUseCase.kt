package co.tuister.domain.usecases

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.repositories.MigrationRepository

class MigrationUseCase(
    private val migrationRepository: MigrationRepository
) : NoParamsUseCase<Boolean> {
    override suspend fun invoke(): Either<Failure, Boolean> {
        return try {
            migrationRepository.migrate()
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
