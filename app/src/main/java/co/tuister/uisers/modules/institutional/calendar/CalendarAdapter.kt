package co.tuister.uisers.modules.institutional.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.netguru.sectionsDecorator.SectionsAdapterInterface
import co.tuister.domain.entities.Event
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseViewHolder
import co.tuister.uisers.utils.DateUtils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_institutional_calendar.*
import java.util.*

class CalendarAdapter(
  private val listener: CalendarListener?
) : RecyclerView.Adapter<CalendarAdapter.EventsViewHolder>(), SectionsAdapterInterface {

    var list = listOf<Event>()
    var sections: List<Pair<String, Int>> = listOf()

    interface CalendarListener {
        fun onClickEvent(event: Event)
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_institutional_calendar, parent, false)
        return EventsViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, listener)
    }

    fun setItems(items: List<Event>) {
        list = items
        sections = items.groupBy { DateUtils.dateToString(Date(it.date)) }.map {
            Pair(it.component1() ?: "", it.component2().size)
        }
        notifyDataSetChanged()
    }

    fun clearItems() {
        val itemCount = list.size
        list = mutableListOf()
        notifyItemRangeRemoved(0, itemCount)
    }

    class EventsViewHolder(view: View) : BaseViewHolder(view), LayoutContainer {
        fun bind(
          event: Event,
          listener: CalendarListener?
        ) {
            text_view_title.text = event.title
            text_view_desc.text = event.description
            itemView.setOnClickListener {
                listener?.onClickEvent(event)
            }
        }
    }

    override fun getItemCountForSection(sectionIndex: Int): Int = sections[sectionIndex].second

    override fun getSectionTitleAt(sectionIndex: Int): String = sections[sectionIndex].first.split("-").getOrNull(2) ?: ""

    override fun getSectionsCount(): Int = sections.size
}
