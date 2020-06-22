package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Place(
    var title: String,
    var desctiption: String = "",
    var latlng: Pair<Double, Double>,
    var siteId: String = ""
) : Parcelable
