package com.example.appdatmon.ui.kitchen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appdatmon.R
import com.example.appdatmon.data.model.KitchenItem
import com.google.android.material.button.MaterialButton

class KitchenAdapter(
    private var itemList: List<KitchenItem>,
    private val onActionClick: (KitchenItem, String) -> Unit
) : RecyclerView.Adapter<KitchenAdapter.KitchenViewHolder>() {

    class KitchenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTableNumber: TextView = itemView.findViewById(R.id.tvTableNumber)
        val tvOrderTime: TextView = itemView.findViewById(R.id.tvOrderTime)
        val tvItemStatus: TextView = itemView.findViewById(R.id.tvItemStatus)
        val ivDishImage: ImageView = itemView.findViewById(R.id.ivDishImage)
        val tvDishName: TextView = itemView.findViewById(R.id.tvDishName)
        val tvDishQuantity: TextView = itemView.findViewById(R.id.tvDishQuantity)
        val layoutNote: LinearLayout = itemView.findViewById(R.id.layoutNote)
        val tvItemNote: TextView = itemView.findViewById(R.id.tvItemNote)
        val btnAction: MaterialButton = itemView.findViewById(R.id.btnAction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitchenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_kitchen_order, parent, false)
        return KitchenViewHolder(view)
    }

    override fun onBindViewHolder(holder: KitchenViewHolder, position: Int) {
        val item = itemList[position]

        val tableNumber = item.Order?.RestaurantTable?.tableNumber
        holder.tvTableNumber.text = if (tableNumber != null) "Bàn số $tableNumber" else "Mang đi"
        holder.tvOrderTime.text = "(Đơn #${item.Order?.id ?: "N/A"})"
        
        holder.tvDishName.text = item.Product?.name ?: "Món ăn"
        holder.tvDishQuantity.text = "Số lượng: ${item.quantity}"

        // Handle Image
        val imageUrl = item.Product?.image
        if (!imageUrl.isNullOrEmpty()) {
            val fullUrl = "http://10.0.2.2:3000$imageUrl"
            Glide.with(holder.itemView.context)
                .load(fullUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivDishImage)
        } else {
            holder.ivDishImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // Handle Note
        if (!item.note.isNullOrEmpty()) {
            holder.layoutNote.visibility = View.VISIBLE
            holder.tvItemNote.text = item.note
        } else {
            holder.layoutNote.visibility = View.GONE
        }

        // Handle status badge and action button based on current status
        when (item.status.uppercase()) {
            "WAITING" -> {
                holder.tvItemStatus.text = "Chờ chế biến"
                holder.tvItemStatus.setBackgroundResource(R.drawable.bg_status_waiting)
                
                holder.btnAction.text = "Bắt đầu nấu"
                holder.btnAction.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_orange_dark))
                
                holder.btnAction.setOnClickListener {
                    onActionClick(item, "COOKING")
                }
            }
            "COOKING" -> {
                holder.tvItemStatus.text = "Đang chế biến"
                holder.tvItemStatus.setBackgroundResource(R.drawable.bg_status_cooking)
                
                holder.btnAction.text = "Hoàn thành"
                holder.btnAction.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark))
                
                holder.btnAction.setOnClickListener {
                    onActionClick(item, "DONE")
                }
            }
            else -> {
                holder.tvItemStatus.text = item.status
                holder.tvItemStatus.setBackgroundResource(R.drawable.bg_status_inactive)
                holder.btnAction.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun updateData(newList: List<KitchenItem>) {
        itemList = newList
        notifyDataSetChanged()
    }
}
