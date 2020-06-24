package co.tuister.uisers.modules.career.subjects

import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.Subject
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseViewHolder
import co.tuister.uisers.utils.extensions.format
import co.tuister.uisers.utils.extensions.getColorFromHex
import co.tuister.uisers.utils.extensions.setTextOrGone
import kotlinx.android.synthetic.main.item_my_career_subject.*

class SubjectsAdapter : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    var list = listOf<Subject>()
    var listener: SubjectListener? = null

    interface SubjectListener {
        fun onClickSubject(subject: Subject)
        fun onClickEdit(subject: Subject)
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_career_subject, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, listener)
    }

    fun setItems(items: List<Subject>) {
        list = items
        notifyDataSetChanged()
    }

    class SubjectViewHolder(view: View) : BaseViewHolder(view), View.OnCreateContextMenuListener {
        fun bind(
            subject: Subject,
            listener: SubjectListener?
        ) {
            text_view_subject_name.text = subject.name
            text_view_subject_desc.setTextOrGone(subject.teacher)
            text_view_subject_note.text = subject.note.format()
            itemView.setOnClickListener {
                listener?.onClickSubject(subject)
            }

            val backgroundColor = context.resources.getIntArray(R.array.colors_100)[subject.materialColor]
            content_view.setBackgroundColor(backgroundColor)
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View,
            menuInfo: ContextMenuInfo?
        ) {
            menu.add(bindingAdapterPosition, 0, 0, v.context.getString(R.string.action_edit))
            menu.add(bindingAdapterPosition, 1, 1, v.context.getString(R.string.action_remove))
        }
    }
}
