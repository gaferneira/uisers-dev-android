package co.tuister.uisers.modules.my_career.schedule

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.SubjectClass
import co.tuister.uisers.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_my_career_schedule.*
import kotlinx.android.synthetic.main.item_my_career_schedule_title.*
import java.util.*

class ScheduleAdapter(
  private val listener: ScheduleListener?
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    var list = listOf<Pair<Int?, SubjectClass?>>()

    interface ScheduleListener {
        fun onClickClass(subjectClass: SubjectClass)
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].first != null) VIEW_TYPE_HEADER else 1
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val layout = if (viewType == 0) {
             R.layout.item_my_career_schedule_title
         } else {
             R.layout.item_my_career_schedule
         }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = list[position]
        val showDivider = when {
            position == 0 -> false
            getItemViewType(position - 1) == VIEW_TYPE_HEADER -> false
            else -> true
        }
        holder.bind(item, showDivider, listener)
    }

    fun setItems(items: List<Pair<Int?, SubjectClass?>>) {
        list = items
        notifyDataSetChanged()
    }

    open class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {

        override val containerView: View?
            get() = itemView

        open fun bind(
          pair: Pair<Int?, SubjectClass?>,
          showDivider: Boolean = true,
          listener: ScheduleListener?
        ) {

            pair.first?.let {
                val calendar = Calendar.getInstance()
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                calendar.set(Calendar.DAY_OF_WEEK, it)

                val today = if (dayOfWeek == it) " (Today)" else ""
                text_view_day.text = "" + DateFormat.format("EEEE", calendar.time) + today
            } ?: run {
                pair.second?.let {
                    divider.isVisible = showDivider
                    text_view_class_name.text = it.subjectName
                    text_view_description.text = it.place
                    text_view_hour.text = it.initialHour + "-" + it.finalHour
                    itemView.setOnClickListener { _ ->
                        listener?.onClickClass(it)
                    }
                }
            }
        }
    }

    companion object {
        const val VIEW_TYPE_HEADER = 0
    }
}
