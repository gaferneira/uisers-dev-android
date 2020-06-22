package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Subject(
    var id: String = "",
    var code: String = "",
    var name: String = "",
    var teacher: String = "",
    var note: Float = 0f,
    var credits: Int = 1
) : Parcelable

@Parcelize
data class CareerSubject(
    var career: String,
    var id: String = "",
    var name: String = "",
    var credits: Int = 1
) : Parcelable
