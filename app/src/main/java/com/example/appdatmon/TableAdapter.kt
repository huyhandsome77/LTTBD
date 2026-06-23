package com.example.appdatmon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

data class RestaurantTable(val number: Int, val status: Int)

class TableAdapter(private val tableList: List<RestaurantTable>) :
    RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTableNumber: TextView = itemView.findViewById(R.id.tvTableNumber)
        val layoutTableBackground: FrameLayout = itemView.findViewById(R.id.layoutTableBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val table = tableList[position]
        holder.tvTableNumber.text = table.number.toString()

        val context = holder.itemView.context
        when (table.status) {
            1 -> holder.layoutTableBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.table_using))
            2 -> holder.layoutTableBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.table_reserved))
            3 -> holder.layoutTableBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.table_empty))
        }
    }

    override fun getItemCount(): Int = tableList.size
}