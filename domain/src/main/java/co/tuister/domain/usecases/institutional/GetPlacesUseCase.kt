package co.tuister.domain.usecases.institutional

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Place
import co.tuister.domain.repositories.MapRepository

class GetPlacesUseCase(
    private val repository: MapRepository
) : NoParamsUseCase<List<Place>> {
    override suspend fun invoke(): Either<Failure, List<Place>> {
        return try {
            repository.getPlaces()
        } catch (e: Exception) {
            Either.Left(Failure.analyzeException(e))
        }
    }
}
