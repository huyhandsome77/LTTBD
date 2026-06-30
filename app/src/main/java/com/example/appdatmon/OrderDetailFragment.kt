package com.example.appdatmon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.data.adapter.OrderItemAdapter
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.Order
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

class OrderDetailFragment : Fragment() {

    private lateinit var tvOrderId: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvCustomer: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvTable: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvPaymentStatus: TextView
    private lateinit var rvItems: RecyclerView
    private lateinit var btnPay: Button
    private lateinit var btnBack: Button

    private var orderId: Long = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvOrderId = view.findViewById(R.id.tv_order_id)
        tvStatus = view.findViewById(R.id.tv_detail_status)
        tvCustomer = view.findViewById(R.id.tv_customer_name)
        tvPhone = view.findViewById(R.id.tv_customer_phone)
        tvTable = view.findViewById(R.id.tv_detail_table)
        tvTotal = view.findViewById(R.id.tv_total_money)
        tvPaymentStatus = view.findViewById(R.id.tv_payment_status)
        rvItems = view.findViewById(R.id.rv_order_items_detail)
        btnPay = view.findViewById(R.id.btn_pay_order_detail)
        btnBack = view.findViewById(R.id.btn_back_order)

        rvItems.layoutManager = LinearLayoutManager(context)

        orderId = arguments?.getLong("order_id_long", -1) ?: -1
        if (orderId != -1L) {
            loadOrderDetail()
        }

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    private fun loadOrderDetail() {
        RetrofitClient.orderApi.getOrderById(orderId).enqueue(object : Callback<Order> {
            override fun onResponse(call: Call<Order>, response: Response<Order>) {
                if (response.isSuccessful && response.body() != null) {
                    val order = response.body()!!
                    displayOrder(order)
                }
            }

            override fun onFailure(call: Call<Order>, t: Throwable) {
                Toast.makeText(context, "Lỗi tải chi tiết đơn hàng", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayOrder(order: Order) {
        tvOrderId.text = "#DH${order.id}"
        tvCustomer.text = order.User?.fullName ?: "Khách vãng lai"
        tvPhone.text = order.User?.phone ?: "N/A"
        tvTable.text = "Bàn: ${order.RestaurantTable?.tableNumber ?: "Mang về"}"
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        tvTotal.text = formatter.format(order.finalPrice)

        // Status
        tvStatus.text = when (order.status) {
            "PENDING" -> "Chờ xác nhận"
            "CONFIRMED" -> "Đã xác nhận"
            "PREPARING" -> "Đang chế biến"
            "READY" -> "Chờ phục vụ"
            "COMPLETED" -> "Hoàn thành"
            "CANCELLED" -> "Đã hủy"
            else -> order.status
        }

        // Payment status
        if (order.paymentStatus == "PAID") {
            tvPaymentStatus.text = "Trạng thái: Đã thanh toán"
            tvPaymentStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            btnPay.visibility = View.GONE
        } else {
            tvPaymentStatus.text = "Trạng thái: Chưa thanh toán"
            tvPaymentStatus.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            btnPay.visibility = View.VISIBLE
            btnPay.setOnClickListener { performPayment(order.id) }
        }

        order.OrderItems?.let {
            rvItems.adapter = OrderItemAdapter(it)
        }
    }

    private fun performPayment(orderId: Long) {
        RetrofitClient.orderApi.payOrder(orderId).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
                    loadOrderDetail() // Reload to update status
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
