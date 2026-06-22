package com.example.appdatmon.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.appdatmon.R

class AdminStatsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_stats, container, false)
        
        val spinnerFilter = view.findViewById<Spinner>(R.id.spinnerTimeFilter)
        val tvRevenue = view.findViewById<TextView>(R.id.tvTotalRevenue)
        val tvOrders = view.findViewById<TextView>(R.id.tvTotalOrders)
        
        // Thiết lập dữ liệu cho Spinner
        val filters = arrayOf("Tuần này", "Tháng này", "Năm nay", "Tất cả")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filters)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapter
        
        // Xử lý sự kiện khi chọn filter
        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Giả lập thay đổi con số khi chọn filter khác nhau
                when (position) {
                    0 -> { // Tuần
                        tvRevenue.text = "25.400.000đ"
                        tvOrders.text = "152 đơn"
                    }
                    1 -> { // Tháng
                        tvRevenue.text = "110.200.000đ"
                        tvOrders.text = "640 đơn"
                    }
                    2 -> { // Năm
                        tvRevenue.text = "1.250.000.000đ"
                        tvOrders.text = "7.200 đơn"
                    }
                    3 -> { // Tất cả
                        tvRevenue.text = "3.500.000.000đ"
                        tvOrders.text = "18.500 đơn"
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        return view
    }
}
