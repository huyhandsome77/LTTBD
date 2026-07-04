package com.example.appdatmon.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.Review
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewAdapter(private var list: MutableList<Review>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCustomerName)
        val tvPhone: TextView = view.findViewById(R.id.tvPhone)
        val tvDish: TextView = view.findViewById(R.id.tvDishName)
        val tvComment: TextView = view.findViewById(R.id.tvComment)
        val tvDate: TextView = view.findViewById(R.id.tvCreatedAt)
        val btnDelete: TextView = view.findViewById(R.id.btnDelete)
        val stars: List<ImageView> = listOf(
            view.findViewById(R.id.adminStar1),
            view.findViewById(R.id.adminStar2),
            view.findViewById(R.id.adminStar3),
            view.findViewById(R.id.adminStar4),
            view.findViewById(R.id.adminStar5)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        
        holder.tvName.text = item.user?.fullName ?: "Khách vãng lai"
        holder.tvPhone.text = item.phone ?: item.user?.phone ?: "N/A"
        holder.tvDish.text = item.dishName ?: "Tổng quát"
        holder.tvComment.text = item.comment
        holder.tvDate.text = formatDateTime(item.createdAt)

        val ratingValue = item.rating
        holder.stars.forEachIndexed { index, imageView ->
            if (index < ratingValue) {
                imageView.setImageResource(R.drawable.ic_star_fill)
                imageView.visibility = View.VISIBLE
            } else {
                imageView.setImageResource(R.drawable.ic_star_empty)
            }
        }

        holder.btnDelete.setOnClickListener {
            deleteReviewOnServer(item.id, position, holder.itemView)
        }
    }

    private fun deleteReviewOnServer(id: Long, position: Int, view: View) {
        RetrofitClient.reviewApi.deleteReview(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    if (position < list.size && list[position].id == id) {
                        list.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, list.size)
                        Toast.makeText(view.context, "Đã xóa đánh giá", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(view.context, "Lỗi khi xóa", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(view.context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatDateTime(rawDate: String?): String {
        if (rawDate == null) return ""
        return try {
            if (rawDate.contains("T")) {
                val parts = rawDate.split("T")
                val date = parts[0]
                val time = parts[1].substring(0, 5)
                "$time - $date"
            } else {
                rawDate
            }
        } catch (e: Exception) {
            rawDate
        }
    }

    override fun getItemCount() = list.size
}