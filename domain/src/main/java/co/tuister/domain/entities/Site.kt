package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Site(
    var id : String,
    var name: String,
    var desctiption: String = "",
    var latlng: Pair<Double, Double>
) : Parcelable