package com.example.appdatmon.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.model.TableOrder

class StaffTableAdapter(
    private var tables: List<TableOrder>,
    private val onDetailClick: (TableOrder) -> Unit
) : RecyclerView.Adapter<StaffTableAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTableName: TextView = view.findViewById(R.id.tvTableName)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val btnViewDetails: Button = view.findViewById(R.id.btnViewDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_staff_table_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val table = tables[position]
        holder.tvTableName.text = table.tableName
        holder.tvOrderStatus.text = table.status

        when (table.status) {
            "Chờ thanh toán" -> holder.tvOrderStatus.textColor("#E65100") // Cam
            "Đã thanh toán" -> holder.tvOrderStatus.textColor("#388E3C") // Xanh lá
            else -> holder.tvOrderStatus.textColor("#1976D2") // Xanh dương đang ăn
        }

        holder.btnViewDetails.setOnClickListener { onDetailClick(table) }
    }

    override fun getItemCount() = tables.size

    fun updateData(newTables: List<TableOrder>) {
        this.tables = newTables
        notifyDataSetChanged()
    }

    private fun TextView.textColor(colorHex: String) {
        this.setTextColor(Color.parseColor(colorHex))
    }
}