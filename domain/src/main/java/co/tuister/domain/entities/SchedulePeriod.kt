package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Calendar

@Parcelize
data class SchedulePeriod(
    var id: String = "",
    var description: String = "",
    var day: Int = Calendar.MONDAY,
    var initialHour: String = "",
    var finalHour: String = "",
    var place: String? = null,
    var materialColor: Int = 0
) : Parcelable
