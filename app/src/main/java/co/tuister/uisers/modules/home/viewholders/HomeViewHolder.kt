package co.tuister.uisers.modules.home.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.FeedAction
import co.tuister.uisers.modules.home.HomeAdapter
import co.tuister.uisers.modules.home.HomeData

open class HomeViewHolder(gngView: View) : RecyclerView.ViewHolder(gngView) {
    open fun bind(
        position: Int,
        homeData: HomeData,
        listener: HomeAdapter.HomeListener?,
        isLastIndex: Boolean
    ) {
        // No op
    }

    fun createDeepLink(deepLink: String) = FeedAction(deepLink = deepLink)
}
