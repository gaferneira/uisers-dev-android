package co.tuister.domain.repositories

import co.tuister.domain.entities.Place
import co.tuister.domain.entities.Site

interface MapRepository {
    suspend fun getPlaces(): List<Place>
    suspend fun getSites(): List<Site>
}
