package co.tuister.uisers.modules.institutional

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.tuister.uisers.R
import kotlinx.android.synthetic.main.item_institutional_menu.view.*

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

    fun clearItems() {
        val itemCount = list.size
        list = mutableListOf()
        notifyItemRangeRemoved(0, itemCount)
    }

    class InstitutionalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(
            position: Int,
            menu: InstitutionalMenu,
            listener: InstitutionalListener
        ) {
            itemView.apply {
                text_view_institutional_menu.setText(menu.title)
                image_view_institutional_menu.setImageResource(menu.icon)
                container_institutional.setBackgroundResource(menu.backgroundColor)
                setOnClickListener {
                    listener.onClickMenu(position)
                }
            }
        }
    }
}
