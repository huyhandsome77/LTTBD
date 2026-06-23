package com.example.appdatmon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class QuanLyDanhMucSanPhamFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Nối file layout XML của quản lý danh mục vào Fragment
        val view = inflater.inflate(R.layout.fragment_quanlydanhmucsanpham, container, false)

        // Ép kiểu cụ thể List<Category> để loại bỏ hoàn toàn lỗi infer type của Kotlin Compiler
        val dummyData: List<Category> = listOf(
            Category("DM01", "Món khai vị", 20),
            Category("DM02", "Món chính", 30),
            Category("DM03", "Tráng miệng", 30),
            Category("DM04", "Nước uống", 15)
        )

        val rvCategory = view.findViewById<RecyclerView>(R.id.rvCategory)
        rvCategory.layoutManager = LinearLayoutManager(context)

        val adapter = CategoryAdapter(dummyData)
        rvCategory.adapter = adapter

        return view
    }
}