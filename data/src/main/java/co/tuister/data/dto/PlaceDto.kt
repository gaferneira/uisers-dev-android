package co.tuister.data.dto

import co.tuister.domain.entities.Place

data class PlaceDto(
    val name: String = "",
    val description: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val site: String = ""
)

fun PlaceDto.toEntity() = Place(name, description, Pair(lat, lon), site)
