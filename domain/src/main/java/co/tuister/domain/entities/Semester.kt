package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Semester(
    val year: Int,
    val period: Int,
    var average: Float = 0f,
    var credits: Int = 0,
    var current: Boolean = false
) : Parcelable