package com.example.appdatmon

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.data.adapter.OrderItemAdapter
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.api.TableResponse
import com.example.appdatmon.data.model.Order
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class QuanLyBanFragment : Fragment() {

    private lateinit var adapter: TableAdapter
    private val tableList = mutableListOf<RestaurantTable>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quanlyban, container, false)

        val rvTables = view.findViewById<RecyclerView>(R.id.rvTables)
        rvTables.layoutManager = GridLayoutManager(context, 3)
        
        adapter = TableAdapter(tableList) { table ->
            showTableDetailDialog(table)
        }
        rvTables.adapter = adapter

        loadTables()

        return view
    }

    private fun loadTables() {
        RetrofitClient.tableApi.getAllTables().enqueue(object : Callback<List<TableResponse>> {
            override fun onResponse(call: Call<List<TableResponse>>, response: Response<List<TableResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    val tables = response.body()!!.map { res ->
                        RestaurantTable(
                            id = res.id,
                            number = res.tableNumber,
                            status = res.calculatedStatus ?: res.status,
                            timeUsed = res.timeUsed,
                            guestCount = res.guestCount ?: res.activeReservation?.numberOfGuests ?: 0,
                            waitingAlert = res.waitingAlert ?: false
                        )
                    }
                    tableList.clear()
                    tableList.addAll(tables)
                    adapter.updateData(tableList)
                } else {
                    Toast.makeText(context, "Lỗi tải danh sách bàn", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TableResponse>>, t: Throwable) {
                Log.e("QuanLyBanFragment", "onFailure: ${t.message}")
                Toast.makeText(context, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showTableDetailDialog(table: RestaurantTable) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_table_detail)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val tvTitle = dialog.findViewById<TextView>(R.id.tvDialogTitle)
        val tvNoOrder = dialog.findViewById<TextView>(R.id.tvNoOrder)
        val layoutOrderInfo = dialog.findViewById<View>(R.id.layoutOrderInfo)
        val rvOrderItems = dialog.findViewById<RecyclerView>(R.id.rvOrderItems)
        val tvTotalAmount = dialog.findViewById<TextView>(R.id.tvTotalAmount)
        val btnPay = dialog.findViewById<Button>(R.id.btnPay)
        val btnClose = dialog.findViewById<Button>(R.id.btnCloseDialog)

        tvTitle.text = "Chi tiết bàn B${String.format("%02d", table.number)}"
        rvOrderItems.layoutManager = LinearLayoutManager(context)

        if (table.status == "OCCUPIED") {
            tvNoOrder.visibility = View.GONE
            layoutOrderInfo.visibility = View.VISIBLE
            btnPay.visibility = View.VISIBLE

            // Tải thông tin hóa đơn từ backend
            RetrofitClient.orderApi.getCurrentOrderByTable(table.id!!).enqueue(object : Callback<Order> {
                override fun onResponse(call: Call<Order>, response: Response<Order>) {
                    if (response.isSuccessful && response.body() != null) {
                        val order = response.body()!!
                        val orderItems = order.OrderItems ?: emptyList()
                        rvOrderItems.adapter = OrderItemAdapter(orderItems)
                        
                        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                        tvTotalAmount.text = formatter.format(order.finalPrice)

                        btnPay.setOnClickListener {
                            performPayment(order.id, dialog)
                        }
                    } else {
                        tvNoOrder.text = "Không tìm thấy hóa đơn hoạt động"
                        tvNoOrder.visibility = View.VISIBLE
                        layoutOrderInfo.visibility = View.GONE
                        btnPay.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<Order>, t: Throwable) {
                    Toast.makeText(context, "Lỗi kết nối khi tải hóa đơn", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            tvNoOrder.visibility = View.VISIBLE
            layoutOrderInfo.visibility = View.GONE
            btnPay.visibility = View.GONE
        }

        btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun performPayment(orderId: Long, dialog: Dialog) {
        RetrofitClient.orderApi.payOrder(orderId).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    loadTables() // Load lại danh sách bàn để cập nhật trạng thái
                } else {
                    Toast.makeText(context, "Thanh toán thất bại", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(context, "Lỗi kết nối khi thanh toán", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
