package co.tuister.domain.usecases.institutional

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Site
import co.tuister.domain.repositories.MapRepository

class GetSitesUseCase(
    private val repository: MapRepository
) : NoParamsUseCase<List<Site>>() {
    override suspend fun run(): Either<Failure, List<Site>> {

        return try {
            Either.Right(repository.getSites())
        } catch (e: Exception) {
            Either.Left(analyzeException(e))
        }

    }
}
