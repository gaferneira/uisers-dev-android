package co.tuister.data.dto

import android.os.Parcelable
import co.tuister.domain.entities.FeedAction
import co.tuister.domain.entities.FeedCard
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

class FeedCardDto(
    var context: String? = null,
    var title: String? = null,
    var body: String? = null,
    var imageUrl: String? = null,
    var cardTemplate: String = "",
    var date: Long = 0L,
    val primaryAction1: FeedActionDto? = null,
    val primaryAction2: FeedActionDto? = null
) : Serializable

fun FeedCardDto.toEntity() = FeedCard(
    context, title, body, imageUrl, cardTemplate, date,
    primaryAction1?.toEntity(), primaryAction2?.toEntity()
)

fun FeedCard.toDTO() = FeedCardDto(
    context, title, body, imageUrl, cardTemplate, date, primaryAction1?.toDTO(), primaryAction2?.toDTO()
)

@Parcelize
data class FeedActionDto(
    val text: String? = "",
    val url: String? = null,
    val deepLink: String? = null
) : Parcelable

fun FeedActionDto.toEntity() = FeedAction(text, url, deepLink)

fun FeedAction.toDTO() = FeedActionDto(text, url, deepLink)
