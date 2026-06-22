package com.example.appdatmon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuanLyBanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ĐÃ SỬA: Đổi fragment_quan_ly_ban thành fragment_quanlyban khớp 100% với file XML của bạn
        val view = inflater.inflate(R.layout.fragment_quanlyban, container, false)

        // Tạo danh sách 12 bàn mẫu chuẩn theo Figma
        val dummyTables = listOf(
            RestaurantTable(1, 1),  // Sử dụng
            RestaurantTable(2, 1),  // Sử dụng
            RestaurantTable(3, 3),  // Trống
            RestaurantTable(4, 2),  // Đã đặt
            RestaurantTable(5, 2),  // Đã đặt
            RestaurantTable(6, 1),  // Sử dụng
            RestaurantTable(7, 1),  // Sử dụng
            RestaurantTable(8, 2),  // Đã đặt
            RestaurantTable(9, 1),  // Sử dụng
            RestaurantTable(10, 2), // Đã đặt
            RestaurantTable(11, 1), // Sử dụng
            RestaurantTable(12, 3)  // Trống
        )

        val rvTables = view.findViewById<RecyclerView>(R.id.rvTables)

        // Cấu hình hiển thị dạng Lưới (Grid) chia làm 3 cột
        rvTables.layoutManager = GridLayoutManager(context, 3)

        // Gắn adapter vào để hiển thị lên màn hình
        rvTables.adapter = TableAdapter(dummyTables)

        return view
    }
}