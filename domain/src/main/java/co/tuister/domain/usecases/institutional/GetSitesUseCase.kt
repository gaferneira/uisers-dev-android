package co.tuister.domain.usecases.institutional

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Site
import co.tuister.domain.repositories.MapRepository

class GetSitesUseCase(
    private val repository: MapRepository
) : NoParamsUseCase<List<Site>> {
    override suspend fun invoke(): Either<Failure, List<Site>> {
        return try {
            repository.getSites()
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
