package com.example.appdatmon.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.model.Review

class ReviewAdapter(private var list: MutableList<Review>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCustomerName)
        val tvDish: TextView = view.findViewById(R.id.tvDishName)
        val tvComment: TextView = view.findViewById(R.id.tvComment)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
        val tvAvatarChar: TextView = view.findViewById(R.id.tvAvatarChar)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvName.text = item.customerName
        holder.tvDish.text = "Món: ${item.dishName}"
        holder.tvComment.text = item.comment
        holder.tvRating.text = if (item.rating.contains("sao")) item.rating.replace(" sao", ".0 ★") else "${item.rating} ★"
        holder.tvAvatarChar.text = item.customerName.take(1).uppercase()

        holder.btnDelete.setOnClickListener {
            list.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, list.size)
        }
    }

    override fun getItemCount() = list.size
}
