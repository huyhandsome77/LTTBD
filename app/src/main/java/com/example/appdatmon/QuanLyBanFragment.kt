package com.example.appdatmon

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuanLyBanFragment : Fragment() {

    private lateinit var adapter: TableAdapter
    private val dummyTables = mutableListOf(
        RestaurantTable(1, 2),  // Sử dụng
        RestaurantTable(2, 2),  // Sử dụng
        RestaurantTable(3, 1),  // Trống
        RestaurantTable(4, 3),  // Đã đặt
        RestaurantTable(5, 3),  // Đã đặt
        RestaurantTable(6, 4),  // Cần dọn
        RestaurantTable(7, 1),  // Trống
        RestaurantTable(8, 1),  // Trống
        RestaurantTable(9, 2)   // Sử dụng
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quanlyban, container, false)
        val rvTables = view.findViewById<RecyclerView>(R.id.rvTables)

        adapter = TableAdapter(dummyTables) { table ->
            showTableActionDialog(table)
        }

        rvTables.layoutManager = GridLayoutManager(context, 3)
        rvTables.adapter = adapter

        return view
    }

    private fun showTableActionDialog(table: RestaurantTable) {
        val actions = arrayOf("Check-in khách", "Chuyển sang Cần dọn", "Đánh dấu Đã dọn xong", "Hủy đặt bàn")
        
        AlertDialog.Builder(context)
            .setTitle("Bàn ${table.number} - Thao tác")
            .setItems(actions) { _, which ->
                when (which) {
                    0 -> { // Check-in
                        table.status = 2
                        Toast.makeText(context, "Đã check-in khách vào bàn ${table.number}", Toast.LENGTH_SHORT).show()
                    }
                    1 -> { // Cần dọn
                        table.status = 4
                    }
                    2 -> { // Dọn xong
                        table.status = 1
                        Toast.makeText(context, "Bàn ${table.number} đã sẵn sàng đón khách", Toast.LENGTH_SHORT).show()
                    }
                    3 -> { // Hủy đặt
                        table.status = 1
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .show()
    }
}