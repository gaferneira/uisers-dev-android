package co.tuister.uisers.modules.home.viewholders

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.children
import co.tuister.domain.entities.Task
import co.tuister.uisers.R
import co.tuister.uisers.modules.home.HomeAdapter
import co.tuister.uisers.modules.home.HomeData
import co.tuister.uisers.modules.home.HomeTasks
import co.tuister.uisers.utils.DateUtils
import co.tuister.uisers.utils.extensions.getColorFromHex
import co.tuister.uisers.utils.extensions.setTextOrGone
import kotlinx.android.synthetic.main.item_home_schedule.view.*
import kotlinx.android.synthetic.main.item_home_task.view.*
import java.util.Date

class TasksViewHolder(view: View) : HomeViewHolder(view) {
    override fun bind(
        position: Int,
        homeData: HomeData,
        listener: HomeAdapter.HomeListener,
        isLastIndex: Boolean
    ) {
        super.bind(position, homeData, listener, isLastIndex)

        val data = homeData as? HomeTasks ?: return

        itemView.container_home_no_items.visibility =
            if (data.list.isNullOrEmpty()) View.VISIBLE else View.GONE

        val container = itemView.container_home_container
        val viewsList = container.children.toMutableList()
        val currentSize = viewsList.size

        val list = data.list?.sortedBy { it.dueDate } ?: listOf()
        val lastIndex = list.lastIndex
        for (i in currentSize..lastIndex) {
            val view = LayoutInflater.from(itemView.context).inflate(R.layout.item_home_task, null)
            container.addView(view)
            viewsList.add(view)
        }

        container.removeAllViews()

        list.forEachIndexed { i, task ->
            container.addView(
                configChildView(viewsList[i], task).apply {
                    setOnClickListener {
                        listener.onClickTask(task)
                    }
                }
            )
        }
    }

    private fun configChildView(view: View, task: Task): View {
        return view.apply {
            text_view_task_name.text = task.title
            text_view_task_hour.text = task.dueDate?.let { DateUtils.dateTimeToString(Date(it), "MMM dd HH:mm") }
            text_view_task_desc.setTextOrGone(task.description)
            val backgroundColor = context.resources.getIntArray(R.array.colors_300)[task.materialColor]
            content_view.setBackgroundColor(backgroundColor)
        }
    }
}
