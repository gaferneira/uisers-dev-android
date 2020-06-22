package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Event(
    var title: String,
    var description: String = "",
    var date: Long,
    var duration: Long? = 0,
    var allDay: Boolean = false,
    var latLng: Pair<Double, Double>? = null
) : Parcelable
