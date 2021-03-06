package co.tuister.data.dto

import co.tuister.domain.entities.SchedulePeriod
import java.io.Serializable

class SchedulePeriodDto(
    var description: String = "",
    var day: Int = 2, // Monday
    var initialHour: String = "",
    var finalHour: String = "",
    var place: String? = null,
    var materialColor: Int = 0
) : Serializable

fun SchedulePeriodDto.toEntity(path: String) = SchedulePeriod(
    path,
    description,
    day,
    initialHour,
    finalHour,
    place,
    materialColor
)

fun SchedulePeriod.toDTO() = SchedulePeriodDto(
    description, day, initialHour, finalHour, place, materialColor
)
