package com.example.appdatmon.ui.User.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.model.Category

class UserCategoryAdapter(
    private val categories: List<Category>,
    private var selectedPosition: Int = 0,
    private val onCategoryClick: (Category, Int) -> Unit
) : RecyclerView.Adapter<UserCategoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.tvName.text = category.name

        if (position == selectedPosition) {
            holder.tvName.setBackgroundResource(R.drawable.bg_category_active)
            holder.tvName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            holder.tvName.setBackgroundResource(R.drawable.bg_category)
            holder.tvName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }

        holder.itemView.setOnClickListener {
            val oldPos = selectedPosition
            selectedPosition = position
            notifyItemChanged(oldPos)
            notifyItemChanged(selectedPosition)
            onCategoryClick(category, position)
        }
    }

    override fun getItemCount() = categories.size
}
