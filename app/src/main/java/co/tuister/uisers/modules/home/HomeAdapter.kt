package co.tuister.uisers.modules.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.FeedAction
import co.tuister.uisers.R
import co.tuister.uisers.modules.home.viewholders.CalendarViewHolder
import co.tuister.uisers.modules.home.viewholders.CardViewHolder
import co.tuister.uisers.modules.home.viewholders.HeaderViewHolder
import co.tuister.uisers.modules.home.viewholders.HomeViewHolder
import co.tuister.uisers.modules.home.viewholders.ScheduleViewHolder
import co.tuister.uisers.modules.home.viewholders.TasksViewHolder

class HomeAdapter(
    private val listener: HomeListener
) : RecyclerView.Adapter<HomeViewHolder>() {

    var list = mutableListOf<HomeData>()

    interface HomeListener {
        fun onClickRow(position: Int, action: FeedAction?)
    }

    enum class HomeEnum {
        HEADER,
        SCHEDULE,
        TASKS,
        CALENDAR,
        CARD;

        fun getLayoutId(): Int {
            return when (this) {
                HEADER -> R.layout.item_home_header
                SCHEDULE -> R.layout.item_home_schedule
                TASKS -> R.layout.item_home_tasks
                CALENDAR -> R.layout.item_home_calendar
                CARD -> R.layout.item_home_card
            }
        }

        fun createViewHolder(view: View): HomeViewHolder {
            return when (this) {
                HEADER -> HeaderViewHolder(view)
                SCHEDULE -> ScheduleViewHolder(view)
                TASKS -> TasksViewHolder(view)
                CALENDAR -> CalendarViewHolder(view)
                CARD -> CardViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val template = HomeEnum.values()[viewType]
        val view =
            LayoutInflater.from(parent.context).inflate(template.getLayoutId(), parent, false)
        return template.createViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val template = list[position].template
        return template.ordinal
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val isLastIndex = position == itemCount - 1
        val item = list[position]
        holder.bind(position, item, listener, isLastIndex)
    }

    fun addItem(item: HomeData) {
        list.add(item)
        list.sortBy { it.template.ordinal }
        notifyDataSetChanged()
    }

    fun addItems(items: List<HomeData>) {
        list.addAll(items)
        list.sortBy { it.template.ordinal }
        notifyDataSetChanged()
    }
    fun clearItems() {
        val itemCount = list.size
        list = mutableListOf()
        notifyItemRangeRemoved(0, itemCount)
    }
}
