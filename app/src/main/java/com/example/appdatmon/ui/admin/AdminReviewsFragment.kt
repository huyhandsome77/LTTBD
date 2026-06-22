package com.example.appdatmon.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.model.Review

class AdminReviewsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_reviews, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rvReviews)
        val tvTotal = view.findViewById<TextView>(R.id.tvTotalReviews)
        val tvAvg = view.findViewById<TextView>(R.id.tvAverageRating)
        
        rv.layoutManager = LinearLayoutManager(context)

        val list = mutableListOf(
            Review(1, "Ronaldo", "Gà rán", "Món ăn rất ngon, phục vụ nhiệt tình!", "5"),
            Review(2, "Messi", "Pizza", "Giao hàng nhanh, pizza còn nóng hổi.", "5"),
            Review(3, "Neymar", "Cơm tấm", "Hơi ít cơm so với giá tiền.", "3"),
            Review(4, "Mbappe", "Bún chả", "Nước dùng hơi mặn một chút.", "4"),
            Review(5, "Salah", "Phở bò", "Tuyệt vời, chuẩn vị truyền thống!", "5")
        )

        tvTotal.text = list.size.toString()
        tvAvg.text = "4.4 ★"

        rv.adapter = ReviewAdapter(list)
        return view
    }
}
