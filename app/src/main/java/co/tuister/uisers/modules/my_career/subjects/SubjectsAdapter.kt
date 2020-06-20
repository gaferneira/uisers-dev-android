package co.tuister.uisers.modules.my_career.subjects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.Subject
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseViewHolder
import co.tuister.uisers.utils.format
import kotlinx.android.synthetic.main.item_my_career_subject.*

class SubjectsAdapter(
    private val listener: SubjectListener?
) : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    var list = listOf<Subject>()

    interface SubjectListener {
        fun onClickSubject(subject: Subject)
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

    class SubjectViewHolder(view: View) : BaseViewHolder(view) {
        fun bind(
            subject: Subject,
            listener: SubjectListener?
        ) {
            text_view_subject_name.text = subject.name
            text_view_subject_desc.text = subject.teacher
            text_view_subject_note.text = subject.note.format()
            itemView.setOnClickListener {
                listener?.onClickSubject(subject)
            }
        }
    }
}
