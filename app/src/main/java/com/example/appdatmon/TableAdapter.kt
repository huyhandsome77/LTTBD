package com.example.appdatmon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

// 1: Trống, 2: Đang dùng, 3: Đã đặt, 4: Cần dọn
data class RestaurantTable(val number: Int, var status: Int)

class TableAdapter(
    private val tableList: List<RestaurantTable>,
    private val onTableClick: (RestaurantTable) -> Unit
) : RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTableNumber: TextView = itemView.findViewById(R.id.tvTableNumber)
        val layoutTableBackground: FrameLayout = itemView.findViewById(R.id.layoutTableBackground)
        val ivStatusIcon: ImageView = itemView.findViewById(R.id.ivStatusIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val table = tableList[position]
        holder.tvTableNumber.text = "Bàn ${table.number}"

        val context = holder.itemView.context
        
        holder.ivStatusIcon.visibility = View.VISIBLE
        
        // Cập nhật giao diện dựa trên trạng thái
        when (table.status) {
            1 -> { // Trống
                holder.layoutTableBackground.setBackgroundResource(R.drawable.bg_table_empty)
                holder.tvTableNumber.setTextColor(ContextCompat.getColor(context, R.color.table_text_dark))
                holder.ivStatusIcon.visibility = View.GONE
            }
            2 -> { // Đang dùng
                holder.layoutTableBackground.setBackgroundResource(R.drawable.bg_table_using)
                holder.tvTableNumber.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.ivStatusIcon.setImageResource(R.drawable.ic_user)
                holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.white))
            }
            3 -> { // Đã đặt
                holder.layoutTableBackground.setBackgroundResource(R.drawable.bg_table_reserved)
                holder.tvTableNumber.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.ivStatusIcon.setImageResource(android.R.drawable.ic_menu_today)
                holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.white))
            }
            4 -> { // Cần dọn
                holder.layoutTableBackground.setBackgroundResource(R.drawable.bg_table_cleaning)
                holder.tvTableNumber.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.ivStatusIcon.setImageResource(R.drawable.ic_cleaning)
                holder.ivStatusIcon.setColorFilter(ContextCompat.getColor(context, R.color.white))
            }
        }

        holder.itemView.setOnClickListener { onTableClick(table) }
    }

    override fun getItemCount(): Int = tableList.size
}