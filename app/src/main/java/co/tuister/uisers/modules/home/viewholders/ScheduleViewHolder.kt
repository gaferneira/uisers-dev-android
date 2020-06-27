package co.tuister.uisers.modules.home.viewholders

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.children
import co.tuister.domain.entities.SchedulePeriod
import co.tuister.uisers.R
import co.tuister.uisers.modules.home.HomeAdapter
import co.tuister.uisers.modules.home.HomeData
import co.tuister.uisers.modules.home.HomeSchedule
import kotlinx.android.synthetic.main.item_career_subject.*
import kotlinx.android.synthetic.main.item_home_schedule.view.*
import kotlinx.android.synthetic.main.item_home_schedule_period.view.*

class ScheduleViewHolder(view: View) : HomeViewHolder(view) {
    override fun bind(
        position: Int,
        homeData: HomeData,
        listener: HomeAdapter.HomeListener?,
        isLastIndex: Boolean
    ) {
        super.bind(position, homeData, listener, isLastIndex)

        val data = homeData as? HomeSchedule ?: return

        itemView.container_home_no_items.visibility =
            if (data.list.isNullOrEmpty()) View.VISIBLE else View.GONE

        val container = itemView.container_home_container
        val viewsList = container.children.toMutableList()
        val currentSize = viewsList.size

        val list = data.list?.sortedBy { it.initialHour } ?: listOf()
        val lastIndex = list.lastIndex
        for (i in currentSize..lastIndex) {
            val view =
                LayoutInflater.from(itemView.context).inflate(R.layout.item_home_schedule_period, null)
            container.addView(view)
            viewsList.add(view)
        }

        container.removeAllViews()

        list.forEachIndexed { i, period ->
            container.addView(
                configChildView(viewsList[i], period).apply {
                    setOnClickListener {
                        listener?.onClickSchedulePeriod(period)
                    }
                }
            )
        }
    }

    private fun configChildView(view: View, period: SchedulePeriod): View {
        return view.apply {
            text_view_subject_name.text = period.description
            text_view_subject_hour.text = period.initialHour + "-" + period.finalHour
            text_view_subject_desc.text = period.place
            val backgroundColor = context.resources.getIntArray(R.array.colors_100)[period.materialColor]
            content_view.setBackgroundColor(backgroundColor)
        }
    }
}
