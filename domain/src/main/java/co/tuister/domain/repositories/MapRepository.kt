package co.tuister.domain.repositories

import co.tuister.domain.base.Either
import co.tuister.domain.base.Failure
import co.tuister.domain.entities.Place
import co.tuister.domain.entities.Site

interface MapRepository {
    suspend fun getPlaces(): Either<Failure, List<Place>>
    suspend fun getSites(): Either<Failure, List<Site>>
}
