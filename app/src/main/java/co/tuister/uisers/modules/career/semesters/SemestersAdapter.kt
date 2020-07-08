package co.tuister.uisers.modules.career.semesters

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.Semester
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseViewHolder
import co.tuister.uisers.utils.extensions.format
import co.tuister.uisers.utils.extensions.singleClick
import kotlinx.android.synthetic.main.item_career_semester.*

class SemestersAdapter(
    private val listener: SemesterListener?
) : RecyclerView.Adapter<SemestersAdapter.SemesterViewHolder>() {

    var list = listOf<Semester>()

    interface SemesterListener {
        fun onClickSemester(semester: Semester)
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemesterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_career_semester, parent, false)
        return SemesterViewHolder(view)
    }

    override fun onBindViewHolder(holder: SemesterViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, listener)
    }

    fun setItems(items: List<Semester>) {
        list = items
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: SemesterViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    class SemesterViewHolder(view: View) : BaseViewHolder(view), View.OnCreateContextMenuListener {

        fun bind(
            semester: Semester,
            listener: SemesterListener?
        ) {
            text_view_semester.text = semester.period
            text_view_average.text = semester.average.format()
            radio_button.isChecked = semester.current
            radio_button.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    radio_button.isChecked = false
                    listener?.onClickSemester(semester)
                }
            }
            itemView.singleClick {
                listener?.onClickSemester(semester)
            }
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun unbind() {
            super.unbind()
            radio_button.setOnCheckedChangeListener(null)
            itemView.setOnClickListener(null)
            itemView.setOnCreateContextMenuListener(null)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu.add(bindingAdapterPosition, v.id, 0, v.context.getString(R.string.base_action_remove))
        }
    }
}
