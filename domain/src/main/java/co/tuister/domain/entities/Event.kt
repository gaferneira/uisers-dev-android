package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class Event(
    var title: String,
    var description: String = "",
    override val date: Date,
    var duration: Long? = 0,
    var allDay: Boolean = false,
    var latLng: Pair<Double, Double>? = null
) : Parcelable, DateWrapper(date) {
    fun isAllDay() = duration?.toInt() == -1
}

@Parcelize
open class DateWrapper(open val date: Date) : Parcelable
