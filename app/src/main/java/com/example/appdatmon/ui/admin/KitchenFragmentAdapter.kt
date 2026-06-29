package com.example.appdatmon.ui.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.model.KitchenItem

class KitchenFragmentAdapter(
    private var itemsList: List<KitchenItem>,
    private val onUpdateClick: (KitchenItem, String) -> Unit
) : RecyclerView.Adapter<KitchenFragmentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTableInfo: TextView = itemView.findViewById(R.id.tvTableInfo)
        val tvFoodNameInfo: TextView = itemView.findViewById(R.id.tvFoodNameInfo)
        val tvQuantityInfo: TextView = itemView.findViewById(R.id.tvQuantityInfo)
        val tvStatusInfo: TextView = itemView.findViewById(R.id.tvStatusInfo)
        val btnUpdateStatus: Button = itemView.findViewById(R.id.btnUpdateStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kitchen_fragment_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemsList[position]

        val tableNum = item.Order?.RestaurantTable?.tableNumber
        holder.tvTableInfo.text = "Bàn: bàn ${tableNum ?: "N/A"}"
        holder.tvFoodNameInfo.text = "Món ăn: ${item.Product?.name ?: "N/A"}"
        holder.tvQuantityInfo.text = "Số lượng: ${item.quantity}"

        when (item.status.uppercase()) {
            "WAITING" -> {
                holder.tvStatusInfo.text = "Chờ chế biến"
                holder.tvStatusInfo.setTextColor(Color.parseColor("#FF8F00")) // Orange
                holder.btnUpdateStatus.text = "Cập nhật trạng thái"
                holder.btnUpdateStatus.visibility = View.VISIBLE
                holder.btnUpdateStatus.setOnClickListener {
                    onUpdateClick(item, "COOKING")
                }
            }
            "COOKING" -> {
                holder.tvStatusInfo.text = "Đang chế biến"
                holder.tvStatusInfo.setTextColor(Color.parseColor("#1565C0")) // Blue
                holder.btnUpdateStatus.text = "Hoàn thành"
                holder.btnUpdateStatus.visibility = View.VISIBLE
                holder.btnUpdateStatus.setOnClickListener {
                    onUpdateClick(item, "DONE")
                }
            }
            else -> {
                holder.tvStatusInfo.text = item.status
                holder.tvStatusInfo.setTextColor(Color.GRAY)
                holder.btnUpdateStatus.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = itemsList.size

    fun updateData(newList: List<KitchenItem>) {
        itemsList = newList
        notifyDataSetChanged()
    }
}
