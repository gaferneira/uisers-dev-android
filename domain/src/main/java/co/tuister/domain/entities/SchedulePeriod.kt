package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SchedulePeriod(
    var id: String = "",
    var description: String = "",
    var day: Int = 2, // Monday
    var initialHour: String = "",
    var finalHour: String = "",
    var place: String? = null
) : Parcelable
