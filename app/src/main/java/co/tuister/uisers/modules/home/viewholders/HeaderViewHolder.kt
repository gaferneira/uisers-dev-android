package co.tuister.uisers.modules.home.viewholders

import android.view.View
import co.tuister.uisers.modules.home.HomeAdapter
import co.tuister.uisers.modules.home.HomeData
import co.tuister.uisers.modules.home.HomeHeader
import kotlinx.android.synthetic.main.item_home_header.view.*
import java.text.SimpleDateFormat
import java.util.*

class HeaderViewHolder(view: View) : HomeViewHolder(view) {
    override fun bind(
        position: Int,
        homeData: HomeData,
        listener: HomeAdapter.HomeListener?,
        isLastIndex: Boolean
    ) {
        super.bind(position, homeData, listener, isLastIndex)

        val data = homeData as? HomeHeader ?: return

        val calendar = Calendar.getInstance()
        val locale = Locale.getDefault()
        val formatNumberMonthToday = SimpleDateFormat("EEEE", locale)
        val formatDateToday = SimpleDateFormat("dd MMMM yyyy", locale)

        itemView.apply {
            text_view_header_date.text = (formatDateToday.format(calendar.time).toUpperCase(locale))
            text_view_header_month.text =
                (formatNumberMonthToday.format(calendar.time).toUpperCase(locale))
            text_view_header_career.text = data.career
            text_view_header_period.text = data.period
        }
    }
}
