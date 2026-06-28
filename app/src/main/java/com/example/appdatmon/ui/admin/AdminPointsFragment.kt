package com.example.appdatmon.ui.admin

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.model.PointRecord
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminPointsFragment : Fragment() {

    private lateinit var adapter: PointAdapter
    private val customerList = mutableListOf(
        PointRecord("1", "Ronaldo", 7),
        PointRecord("2", "Messi", 10)
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_points, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rvPoints)
        val btnAddCustomer = view.findViewById<FloatingActionButton>(R.id.btnAddCustomer)
        val edtSearch = view.findViewById<EditText>(R.id.edtSearch)
        
        rv.layoutManager = LinearLayoutManager(context)

        // Logic xử lý khi nhấn nút lịch sử và cộng điểm
        adapter = PointAdapter(
            list = customerList,
            onUpdatePoints = { record, change ->
                // LOGIC CỘNG ĐIỂM THẬT: Cập nhật vào list dữ liệu
                val index = customerList.indexOfFirst { it.id == record.id }
                if (index != -1) {
                    val updatedRecord = customerList[index].copy(points = customerList[index].points + change)
                    customerList[index] = updatedRecord
                    adapter.notifyItemChanged(index)
                }
            },
            onViewHistory = { record ->
                // LOGIC LỊCH SỬ THẬT: Chỉ hiện của người được chọn
                val historyFragment = AdminPointHistoryFragment()
                val bundle = Bundle()
                bundle.putString("customerName", record.customerName)
                historyFragment.arguments = bundle
                
                parentFragmentManager.beginTransaction()
                    .replace(R.id.content_container, historyFragment)
                    .addToBackStack(null)
                    .commit()
            }
        )
        rv.adapter = adapter

        // Logic Lọc theo tên
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnAddCustomer.setOnClickListener {
            showAddCustomerDialog()
        }

        return view
    }

    private fun filter(query: String) {
        val filteredList = customerList.filter { 
            it.customerName.contains(query, ignoreCase = true) || it.id.contains(query)
        }
        adapter.updateList(filteredList)
    }

    private fun showAddCustomerDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_customer, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val edtName = dialogView.findViewById<EditText>(R.id.edtCustomerName)
        val edtId = dialogView.findViewById<EditText>(R.id.edtCustomerId)
        val edtPoints = dialogView.findViewById<EditText>(R.id.edtInitialPoints)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnConfirm.setOnClickListener {
            val name = edtName.text.toString()
            val id = edtId.text.toString()
            val pointsStr = edtPoints.text.toString()
            val points = if (pointsStr.isNotEmpty()) pointsStr.toInt() else 0

            if (name.isNotEmpty() && id.isNotEmpty()) {
                val newCustomer = PointRecord(id, name, points)
                customerList.add(newCustomer)
                
                filter("") 
                
                val rv = view?.findViewById<RecyclerView>(R.id.rvPoints)
                rv?.scrollToPosition(customerList.size - 1)

                Toast.makeText(context, "Thêm khách hàng $name thành công!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
