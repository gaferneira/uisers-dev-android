package co.tuister.uisers.modules.career

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseViewHolder
import co.tuister.uisers.utils.extensions.format
import kotlinx.android.synthetic.main.item_career_footer.*

class FooterAdapter : RecyclerView.Adapter<FooterAdapter.FooterViewHolder>() {

    data class FooterData(var title: Int?, var total: Number?)

    private val footerData = FooterData(null, null)

    override fun getItemCount() = if (footerData.total != null) {
        1
    } else {
        0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_career_footer, parent, false)
        return FooterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FooterViewHolder, position: Int) {
        holder.bind(footerData)
    }

    fun setData(title: Int, total: Number?) {
        footerData.title = title
        footerData.total = total
        notifyDataSetChanged()
    }

    class FooterViewHolder(view: View) : BaseViewHolder(view) {
        fun bind(data: FooterData?) {
            text_view_footer_title.text = data?.title?.let { context.getString(it) }
            text_view_footer_total.text = data?.total?.format()
        }
    }
}
