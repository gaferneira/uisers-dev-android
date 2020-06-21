package co.tuister.uisers.modules.task_manager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.Task
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseViewHolder
import kotlinx.android.synthetic.main.item_tasks_task.*

class TasksAdapter(
    private val listener: TasksListener?
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
        val item = list[position]
        holder.bind(item, listener)
    }

    fun setItems(items: List<Task>) {
        list = items
        notifyDataSetChanged()
    }

    class TasksViewHolder(view: View) : BaseViewHolder(view) {
        fun bind(
            task: Task,
            listener: TasksListener?
        ) {
            text_view_task_name.text = task.title
            text_view_task_desc.text = task.description
            itemView.setOnClickListener {
                listener?.onClickTask(task)
            }
        }
    }
}
