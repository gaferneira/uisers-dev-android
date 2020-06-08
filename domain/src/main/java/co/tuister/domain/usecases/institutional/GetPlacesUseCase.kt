package co.tuister.domain.usecases.institutional

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.base.NoParamsUseCase
import co.tuister.domain.entities.Place
import co.tuister.domain.repositories.MapRepository

class GetPlacesUseCase(
    private val repository: MapRepository
) : NoParamsUseCase<List<Place>>() {
    override suspend fun run(): Either<Failure, List<Place>> {
        return repository.getPlaces()
    }
}