package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Subject(
    var name: String = "",
    var teacher: String? = null,
    var note: Float = 0f,
    var credits: Int = 1
) : Parcelable