package co.tuister.uisers.modules.my_career.subject_details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.Note
import co.tuister.uisers.R
import co.tuister.uisers.utils.format
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_my_career_note.*

class NotesAdapter(
    private val listener: NoteListener
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    var list = listOf<Note>()

    interface NoteListener {
        fun onClickNote(note: Note)
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_career_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item, listener)
    }

    fun setItems(items: List<Note>) {
        list = items
        notifyDataSetChanged()
    }

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {

        override val containerView: View?
            get() = itemView

        fun bind(
            note: Note,
            listener: NoteListener
        ) {
            text_view_note_desc.text = note.title
            text_view_note_grade.text = note.grade.format()
            text_view_note_percentage.text = note.percentage.format(1)
            text_view_note_total.text = note.total.format()

            val color = if (note.grade >= 3) R.color.green_700 else R.color.red_700

            text_view_note_grade.setTextColor(
                ResourcesCompat.getColor(
                    itemView.resources,
                    color,
                    itemView.context.theme
                )
            )

            itemView.setOnClickListener {
                listener.onClickNote(note)
            }
        }
    }
}