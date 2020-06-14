package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    var id : String = "",
    var title: String = "",
    var grade: Float = 0f,
    var percentage: Float = 0f,
    var total: Float = 1f
) : Parcelable