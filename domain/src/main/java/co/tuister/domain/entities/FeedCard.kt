package co.tuister.domain.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FeedCard(
    var context: String? = null,
    var title: String? = null,
    var body: String? = null,
    var imageUrl: String? = null,
    var cardTemplate: String = "",
    var date: Long = 0L,
    val primaryAction1: FeedAction? = null,
    val primaryAction2: FeedAction? = null
) : Parcelable

@Parcelize
data class FeedAction(
    val text: String? = "",
    val url: String? = null,
    val deepLink: String? = null
) : Parcelable
