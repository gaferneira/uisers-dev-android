package co.tuister.uisers.modules.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.SubjectClass
import co.tuister.domain.entities.Task
import co.tuister.uisers.R
import co.tuister.uisers.modules.home.viewholders.HeaderViewHolder
import co.tuister.uisers.modules.home.viewholders.HomeViewHolder
import co.tuister.uisers.modules.home.viewholders.SubjectsViewHolder
import co.tuister.uisers.modules.home.viewholders.TasksViewHolder

class HomeAdapter(
  private val listener: HomeListener
) : RecyclerView.Adapter<HomeViewHolder>() {

    var list = mutableListOf<HomeData>()

    interface HomeListener {
        fun onClickRow(position: Int)
        fun onClickSubjectClass(subjectClass: SubjectClass)
        fun onClickTask(task: Task)
    }

    enum class HomeEnum {
        HEADER,
        SUBJECT,
        TASKS;

        fun getLayoutId(): Int {
            return when (this) {
                HEADER -> R.layout.item_home_header
                SUBJECT -> R.layout.item_home_subjects
                TASKS -> R.layout.item_home_tasks
            }
        }

        fun createViewHolder(view: View): HomeViewHolder {
            return when (this) {
                HEADER -> HeaderViewHolder(view)
                SUBJECT -> SubjectsViewHolder(view)
                TASKS -> TasksViewHolder(view)
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

    fun clearItems() {
        val itemCount = list.size
        list = mutableListOf()
        notifyItemRangeRemoved(0, itemCount)
    }
}
