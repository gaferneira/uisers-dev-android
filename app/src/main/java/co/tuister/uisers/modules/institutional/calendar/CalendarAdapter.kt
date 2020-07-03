package co.tuister.uisers.modules.institutional.calendar

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.DateWrapper
import co.tuister.domain.entities.Event
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseViewHolder
import co.tuister.uisers.utils.DateUtils
import co.tuister.uisers.utils.sectionsDecorator.SectionsAdapterInterface
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.item_institutional_calendar.*
import java.util.Date

class CalendarAdapter(
    private val listener: CalendarListener?
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>(), SectionsAdapterInterface {

    var list = mutableListOf<DateWrapper>()
    private var sections = mutableListOf<Pair<String, Int>>()

    interface CalendarListener {
        fun onClickEvent(event: Event)
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val layoutId = if (viewType == TYPE_MONTH) R.layout.item_institutional_calendar_month else R.layout.item_institutional_calendar
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return if (viewType == TYPE_MONTH) MonthViewHolder(view) else EventsViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is MonthLabel) TYPE_MONTH else TYPE_EVENT
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, listener)
    }

    fun setItems(items: List<Event>) {

        list.clear()
        sections.clear()

        if (items.isEmpty()) {
            notifyDataSetChanged()
            return
        }

        var currentMonth = ""
        var currentDay = getLabelDay(items.first().date) ?: ""
        var eventsCount = 0

        items.forEach {

            getLabelMonth(it.date)?.let { month ->
                if (month != currentMonth) {
                    list.add(MonthLabel(it.date))
                    sections.add(Pair("", 1))
                    currentMonth = month
                }
            }

            eventsCount++
            getLabelDay(it.date)?.let { day ->
                if (currentDay != day) {
                    sections.add(Pair(currentDay, eventsCount))
                    currentDay = day
                    eventsCount = 0
                }
            }

            list.add(it)
        }

        notifyDataSetChanged()
    }

    private fun getLabelMonth(date: Date) = DateUtils.dateToString(date, "yyyy-MM")
    private fun getLabelDay(date: Date) = DateUtils.dateToString(date, "EEE-yy")?.replace("-", "\n")

    fun clearItems() {
        val itemCount = list.size
        list.clear()
        notifyItemRangeRemoved(0, itemCount)
    }

    override fun getItemCountForSection(sectionIndex: Int): Int = sections[sectionIndex].second

    override fun getSectionTitleAt(sectionIndex: Int): String = sections[sectionIndex].first

    override fun getSectionsCount(): Int = sections.size

    abstract class CalendarViewHolder(view: View) : BaseViewHolder(view) {
        abstract fun bind(item: DateWrapper, listener: CalendarListener?)
    }

    class EventsViewHolder(view: View) : CalendarViewHolder(view) {
        override fun bind(
            item: DateWrapper,
            listener: CalendarListener?
        ) {
            val event = item as Event
            text_view_title.text = event.title
            text_view_desc.text = event.description
            itemView.setOnClickListener {
                listener?.onClickEvent(event)
            }
        }
    }

    class MonthViewHolder(view: View) : CalendarViewHolder(view) {
        override fun bind(
            item: DateWrapper,
            listener: CalendarListener?
        ) {
            text_view_title.text = DateUtils.dateToString(item.date, "MMMM")
            itemView.setOnClickListener(null)
        }
    }

    companion object {
        const val TYPE_MONTH = 0
        const val TYPE_EVENT = 1
    }
}

@Parcelize
data class MonthLabel(override val date: Date) : Parcelable, DateWrapper(date)
