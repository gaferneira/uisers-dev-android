package co.tuister.uisers.modules.home.viewholders

import android.view.View
import androidx.core.view.isVisible
import co.tuister.uisers.modules.home.HomeAdapter
import co.tuister.uisers.modules.home.HomeCard
import co.tuister.uisers.modules.home.HomeData
import co.tuister.uisers.utils.extensions.setImageFromUrl
import co.tuister.uisers.utils.extensions.setTextOrGone
import kotlinx.android.synthetic.main.item_home_card.view.*

class CardViewHolder(view: View) : HomeViewHolder(view) {
    override fun bind(
        position: Int,
        homeData: HomeData,
        listener: HomeAdapter.HomeListener?,
        isLastIndex: Boolean
    ) {
        super.bind(position, homeData, listener, isLastIndex)

        val data = homeData as? HomeCard ?: return

        itemView.apply {
            text_card_context.setTextOrGone(data.context)
            text_card_title.setTextOrGone(data.title)
            text_card_body.setTextOrGone(data.body)

            image_view_card.isVisible = !data.imageUrl.isNullOrBlank()
            image_view_card.setImageFromUrl(data.imageUrl)

            button_card_one.isVisible = data.primaryAction1 != null
            button_card_two.isVisible = data.primaryAction2 != null

            button_card_one.text = data.primaryAction1?.text
            button_card_two.text = data.primaryAction2?.text

            button_card_one.setOnClickListener {
                listener?.onClickRow(position, data.primaryAction1)
            }

            button_card_two.setOnClickListener {
                listener?.onClickRow(position, data.primaryAction2)
            }
        }
    }
}
