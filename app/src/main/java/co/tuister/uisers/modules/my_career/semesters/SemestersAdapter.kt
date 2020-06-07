package co.tuister.uisers.modules.my_career.semesters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.tuister.domain.entities.Semester
import co.tuister.uisers.R
import co.tuister.uisers.utils.format
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_my_career_semester.*

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
            .inflate(R.layout.item_my_career_semester, parent, false)
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

    class SemesterViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {

        override val containerView: View?
            get() = itemView

        fun bind(
          semester: Semester,
          listener: SemesterListener?
        ) {
            text_view_semester.text = "" + semester.year + "-" + semester.period
            text_view_average.text = semester.average.format()
            radio_button.setOnCheckedChangeListener { compoundButton, checked ->
                if (checked) {
                    radio_button.isChecked = false
                    listener?.onClickSemester(semester)
                }
            }
            itemView.setOnClickListener {
                listener?.onClickSemester(semester)
            }
        }
    }
}
