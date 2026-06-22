package com.example.appdatmon.ui.admin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.model.PointHistory

class PointHistoryAdapter(private var list: List<PointHistory>) : RecyclerView.Adapter<PointHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvAction: TextView = view.findViewById(R.id.tvAction)
        val ivIcon: ImageView = view.findViewById(R.id.ivActionIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_point_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvName.text = "Khách hàng: ${item.customerName}"
        holder.tvTime.text = item.time
        holder.tvAction.text = item.action
        
        // Đổi màu icon và chữ dựa trên hành động
        if (item.action.contains("cộng", ignoreCase = true)) {
            holder.tvAction.setTextColor(Color.parseColor("#2E7D32")) // Xanh
            holder.ivIcon.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#E8F5E9"))
            holder.ivIcon.setColorFilter(Color.parseColor("#2E7D32"))
        } else {
            holder.tvAction.setTextColor(Color.parseColor("#D32F2F")) // Đỏ
            holder.ivIcon.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#FFEBEE"))
            holder.ivIcon.setColorFilter(Color.parseColor("#D32F2F"))
        }
    }

    override fun getItemCount() = list.size
}
