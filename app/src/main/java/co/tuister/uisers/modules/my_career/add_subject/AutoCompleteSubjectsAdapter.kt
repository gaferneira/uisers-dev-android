package co.tuister.uisers.modules.my_career.add_subject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import co.tuister.domain.entities.Subject

class AutoCompleteSubjectsAdapter(context: Context, items: List<Subject>?) :
    ArrayAdapter<Subject>(context, android.R.layout.simple_list_item_1, items ?: listOf()),
    Filterable {

    var filtered: List<Subject> = listOf()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return convertView ?: createView(position, parent)
    }

    private fun createView(position: Int, parent: ViewGroup?): View {
        val view =
            LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        (view as? TextView)?.text = filtered[position].name
        return view
    }

    override fun getCount() = filtered.size

    override fun getItem(position: Int) = filtered[position]

    override fun getFilter() = filter

    private var filter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()

            val query = when {
                constraint.isNullOrEmpty() || items.isNullOrEmpty() -> listOf()
                else -> items.filter { it.name.contains(constraint, ignoreCase = true) }
            }

            results.values = query
            results.count = query.size

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            filtered = results.values as List<Subject>
            notifyDataSetInvalidated()
        }

        override fun convertResultToString(result: Any) = (result as Subject).name
    }
}
