package co.tuister.uisers.modules.home.viewholders

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.children
import co.tuister.domain.entities.SubjectClass
import co.tuister.uisers.R
import co.tuister.uisers.modules.home.HomeAdapter
import co.tuister.uisers.modules.home.HomeData
import co.tuister.uisers.modules.home.HomeSubjects
import kotlinx.android.synthetic.main.item_home_subject.view.*
import kotlinx.android.synthetic.main.item_home_subjects.view.*

class SubjectsViewHolder(view: View) : HomeViewHolder(view) {
    override fun bind(
        position: Int,
        homeData: HomeData,
        listener: HomeAdapter.HomeListener,
        isLastIndex: Boolean
    ) {
        super.bind(position, homeData, listener, isLastIndex)

        val data = homeData as? HomeSubjects ?: return

        itemView.container_home_no_items.visibility =
            if (data.list.isNullOrEmpty()) View.VISIBLE else View.GONE

        val container = itemView.container_home_container
        val viewsList = container.children.toMutableList()
        val currentSize = viewsList.size

        val lastIndex = data.list?.lastIndex ?: 0
        for (i in currentSize..lastIndex) {
            val view =
                LayoutInflater.from(itemView.context).inflate(R.layout.item_home_subject, null)
            container.addView(view)
            viewsList.add(view)
        }

        container.removeAllViews()

        data.list?.forEachIndexed { i, timeTable ->
            container.addView(configChildView(viewsList[i], timeTable).apply {
                setOnClickListener {
                    listener.onClickSubject(timeTable.subject)
                }
            })
        }
    }

    private fun configChildView(view: View, timeTable: SubjectClass): View {
        return view.apply {
            text_view_subject_name.text = timeTable.subject.name
            text_view_subject_hour.text = timeTable.initialHour + "-" + timeTable.finalHour
            text_view_subject_desc.text = timeTable.place
        }
    }
}
