package com.example.appdatmon.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.api.ReviewListResponse
import com.example.appdatmon.data.model.Review
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminReviewsFragment : Fragment() {
    
    private lateinit var rv: RecyclerView
    private var reviewList = mutableListOf<Review>()
    private lateinit var adapter: ReviewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_reviews, container, false)
        rv = view.findViewById(R.id.rvReviews)
        rv.layoutManager = LinearLayoutManager(context)
        
        adapter = ReviewAdapter(reviewList)
        rv.adapter = adapter

        fetchReviews()
        
        return view
    }

    private fun fetchReviews() {
        RetrofitClient.reviewApi.getAllReviews().enqueue(object : Callback<ReviewListResponse> {
            override fun onResponse(call: Call<ReviewListResponse>, response: Response<ReviewListResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    data?.reviews?.let {
                        reviewList.clear()
                        reviewList.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(context, "Không thể tải đánh giá", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ReviewListResponse>, t: Throwable) {
                Log.e("AdminReviewsFragment", "Error: ${t.message}")
                Toast.makeText(context, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
            }
        })
    }
}