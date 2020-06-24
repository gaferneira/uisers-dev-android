package co.tuister.data.dto

import co.tuister.domain.entities.SchedulePeriod
import java.io.Serializable

class SchedulePeriodDto(
    var description: String = "",
    var day: Int = 2, // Monday
    var initialHour: String = "",
    var finalHour: String = "",
    var place: String? = null,
    var color: String? = null
) : Serializable

fun SchedulePeriodDto.toEntity(path: String) = SchedulePeriod(
    path,
    description,
    day,
    initialHour,
    finalHour,
    place,
    color
)

fun SchedulePeriod.toDTO() = SchedulePeriodDto(
    description, day, initialHour, finalHour, place, color
)
