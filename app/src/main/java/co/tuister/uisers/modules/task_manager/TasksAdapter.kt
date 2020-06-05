package co.tuister.uisers.modules.task_manager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.Task
import co.tuister.uisers.R
import kotlinx.android.synthetic.main.item_tasks_task.view.*

class TasksAdapter(
    private val listener: TasksListener
) : RecyclerView.Adapter<TasksAdapter.TasksViewHolder>() {

    var list = listOf<Task>()

    interface TasksListener {
        fun onClickTask(task: Task)
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tasks_task, parent, false)
        return TasksViewHolder(view)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val isLastIndex = position == itemCount - 1
        val item = list[position]
        holder.bind(position, item, listener, isLastIndex)
    }

    fun setItems(items: List<Task>) {
        list = items
        notifyDataSetChanged()
    }

    fun clearItems() {
        val itemCount = list.size
        list = mutableListOf()
        notifyItemRangeRemoved(0, itemCount)
    }

    class TasksViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            position: Int,
            task: Task,
            listener: TasksListener,
            isLastIndex: Boolean
        ) {
            itemView.apply {
                text_view_task_name.text = task.title
                text_view_task_desc.text = task.description
            }
        }
    }
}
