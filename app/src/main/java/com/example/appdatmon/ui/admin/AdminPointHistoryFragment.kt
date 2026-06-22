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
import com.example.appdatmon.data.model.PointHistory

class AdminPointHistoryFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_point_history, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rvHistory)
        val tvTitle = view.findViewById<TextView>(R.id.tvHistoryTitle)
        
        rv.layoutManager = LinearLayoutManager(context)

        // Lấy tên khách hàng từ Arguments
        val customerName = arguments?.getString("customerName") ?: ""
        
        // Cập nhật tiêu đề nếu cần
        if (customerName.isNotEmpty()) {
            tvTitle?.text = "Lịch sử của $customerName"
        }

        val allHistory = listOf(
            PointHistory("1", "Ronaldo", "1/6/2026", "Đã cộng 7 điểm"),
            PointHistory("1", "Ronaldo", "2/6/2026", "Đã cộng 3 điểm"),
            PointHistory("2", "Messi", "1/6/2026", "Đã cộng 10 điểm"),
            PointHistory("2", "Messi", "5/6/2026", "Đã trừ 2 điểm")
        )

        // Lọc danh sách: Chỉ lấy những dòng có tên khớp với khách hàng được chọn
        val filteredList = if (customerName.isNotEmpty()) {
            allHistory.filter { it.customerName == customerName }
        } else {
            allHistory
        }

        rv.adapter = PointHistoryAdapter(filteredList)
        return view
    }
}
