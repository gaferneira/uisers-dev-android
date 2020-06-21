package co.tuister.uisers.modules.institutional

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.tuister.uisers.R
import co.tuister.uisers.common.BaseViewHolder
import kotlinx.android.synthetic.main.item_institutional_menu.*

class InstitutionalAdapter(
    private val listener: InstitutionalListener
) : RecyclerView.Adapter<InstitutionalAdapter.InstitutionalViewHolder>() {

    var list = listOf<InstitutionalMenu>()

    interface InstitutionalListener {
        fun onClickMenu(position: Int)
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstitutionalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_institutional_menu, parent, false)
        return InstitutionalViewHolder(view)
    }

    override fun onBindViewHolder(holder: InstitutionalViewHolder, position: Int) {
        val item = list[position]
        holder.bind(position, item, listener)
    }

    fun setItems(items: List<InstitutionalMenu>) {
        list = items
        notifyDataSetChanged()
    }

    class InstitutionalViewHolder(view: View) : BaseViewHolder(view) {
        fun bind(
            position: Int,
            menu: InstitutionalMenu,
            listener: InstitutionalListener
        ) {
            text_view_institutional_menu.setText(menu.title)
            image_view_institutional_menu.setImageResource(menu.icon)
            container_institutional.setBackgroundResource(menu.backgroundColor)
            itemView.setOnClickListener {
                listener.onClickMenu(position)
            }
        }
    }
}
