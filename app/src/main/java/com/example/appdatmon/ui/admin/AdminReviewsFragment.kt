package com.example.appdatmon.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.model.Review

class AdminReviewsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_reviews, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rvReviews)
        rv.layoutManager = LinearLayoutManager(context)

        val list = mutableListOf(
            Review(1, "Ronaldo", "Gà", "Mặn vchhhh", "4 sao"),
            Review(2, "Messi", "Gà", "Ngon", "5 sao"),
            Review(3, "Neymar", "Cơm", "Rất tốt", "5 sao"),
            Review(4, "Mbappe", "Bún chả", "Hơi cay", "4 sao"),
            Review(5, "Salah", "Phở", "Tuyệt vời", "5 sao")
        )

        rv.adapter = ReviewAdapter(list)
        return view
    }
}