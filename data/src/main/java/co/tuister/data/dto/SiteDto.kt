package co.tuister.data.dto

import co.tuister.domain.entities.Site

data class SiteDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

fun SiteDto.toEntity() = Site(id, name, description, Pair(lat, lon))
