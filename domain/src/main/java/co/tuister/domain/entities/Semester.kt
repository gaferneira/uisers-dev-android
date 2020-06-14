package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Semester(
    val period: String,
    var average: Float = 0f,
    var credits: Int = 0,
    var current: Boolean = false
) : Parcelable