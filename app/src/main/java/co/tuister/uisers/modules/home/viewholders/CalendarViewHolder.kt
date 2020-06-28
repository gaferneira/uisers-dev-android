package co.tuister.uisers.modules.home.viewholders

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.children
import co.tuister.domain.entities.Event
import co.tuister.uisers.R
import co.tuister.uisers.modules.home.HomeAdapter
import co.tuister.uisers.modules.home.HomeCalendar
import co.tuister.uisers.modules.home.HomeData
import co.tuister.uisers.utils.DateUtils
import co.tuister.uisers.utils.extensions.setTextOrGone
import kotlinx.android.synthetic.main.item_home_calelendar_event.view.*
import kotlinx.android.synthetic.main.item_home_calendar.view.*
import java.util.Date

class CalendarViewHolder(view: View) : HomeViewHolder(view) {
    override fun bind(
        position: Int,
        homeData: HomeData,
        listener: HomeAdapter.HomeListener?,
        isLastIndex: Boolean
    ) {
        super.bind(position, homeData, listener, isLastIndex)

        val data = homeData as? HomeCalendar ?: return

        itemView.container_home_no_items.visibility =
            if (data.list.isNullOrEmpty()) View.VISIBLE else View.GONE

        val container = itemView.container_home_container
        val viewsList = container.children.toMutableList()
        val currentSize = viewsList.size

        val list = data.list?.sortedBy { it.date } ?: listOf()
        val lastIndex = list.lastIndex
        for (i in currentSize..lastIndex) {
            val view = LayoutInflater.from(itemView.context).inflate(R.layout.item_home_calelendar_event, null)
            container.addView(view)
            viewsList.add(view)
        }

        container.removeAllViews()

        list.forEachIndexed { i, item ->
            container.addView(
                configChildView(viewsList[i], item).apply {
                    setOnClickListener {
                        listener?.onClickRow(position, createDeepLink(context.getString(R.string.deep_link_institution_calendar)))
                    }
                }
            )
        }
    }

    private fun configChildView(view: View, item: Event): View {
        return view.apply {
            text_view_event_name.text = item.title
            text_view_event_date.text = DateUtils.dateTimeToString(
                Date(item.date),
                if (item.isAllDay()) "MMM dd" else "MMM dd HH:mm"
            )
            text_view_event_desc.setTextOrGone(item.description)
        }
    }
}
