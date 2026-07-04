package com.example.appdatmon

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    
    private lateinit var layoutAdminActions: View
    private lateinit var btnConfirmOrder: Button
    private lateinit var btnCancelOrder: Button

    private lateinit var layoutPayment: View
    private lateinit var rgMethod: android.widget.RadioGroup
    private lateinit var layoutQR: View
    private lateinit var ivQR: android.widget.ImageView

    private var orderId: Long = -1

    private var isCheckingStatus = false
    private val handler = Handler(Looper.getMainLooper())
    private var checkStatusRunnable: Runnable? = null

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
        
        layoutAdminActions = view.findViewById(R.id.layout_admin_actions)
        btnConfirmOrder = view.findViewById(R.id.btn_confirm_order)
        btnCancelOrder = view.findViewById(R.id.btn_cancel_order)

        layoutPayment = view.findViewById(R.id.layout_payment_selection)
        rgMethod = view.findViewById(R.id.rg_detail_payment_method)
        layoutQR = view.findViewById(R.id.layout_detail_qr)
        ivQR = view.findViewById(R.id.iv_detail_qr)

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

        tvStatus.text = when (order.status) {
            "PENDING" -> "Chờ xác nhận"
            "CONFIRMED" -> "Đã xác nhận"
            "PREPARING" -> "Đang chế biến"
            "READY" -> "Chờ phục vụ"
            "COMPLETED" -> "Hoàn thành"
            "CANCELLED" -> "Đã hủy"
            else -> order.status
        }

        // Logic hiển thị nút bấm theo quy trình mới
        if (order.status == "PENDING") {
            layoutAdminActions.visibility = View.VISIBLE
            btnConfirmOrder.setOnClickListener { updateStatus(order.id, "CONFIRMED") }
            btnCancelOrder.setOnClickListener { updateStatus(order.id, "CANCELLED") }
        } else {
            layoutAdminActions.visibility = View.GONE
        }

        // Chỉ cho phép thanh toán nếu đang ở trạng thái READY
        if (order.status == "READY" && order.paymentStatus != "PAID") {
            tvPaymentStatus.text = "Trạng thái: Chờ thanh toán"
            tvPaymentStatus.setTextColor(resources.getColor(android.R.color.holo_orange_dark, null))
            
            btnPay.visibility = View.VISIBLE
            layoutPayment.visibility = View.VISIBLE

            rgMethod.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId == R.id.rb_detail_transfer) {
                    layoutQR.visibility = View.VISIBLE
                    btnPay.text = "XÁC NHẬN ĐA NHẬN TIỀN"
                    loadQR(order.id)
                    startAutoStatusCheck(order.id)
                } else {
                    layoutQR.visibility = View.GONE
                    btnPay.text = "THANH TOÁN TIỀN MẶT"
                    stopStatusCheck()
                }
            }

            btnPay.setOnClickListener { 
                val method = if (rgMethod.checkedRadioButtonId == R.id.rb_detail_transfer) "TRANSFER" else "CASH"
                performPayment(order.id, method) 
            }
        } else if (order.paymentStatus == "PAID") {
            tvPaymentStatus.text = "Trạng thái: Đã thanh toán"
            tvPaymentStatus.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            btnPay.visibility = View.GONE
            layoutPayment.visibility = View.GONE
            stopStatusCheck()
        } else {
            // Trường hợp đang CONFIRMED hoặc PREPARING: Chưa cho thanh toán
            btnPay.visibility = View.GONE
            layoutPayment.visibility = View.GONE
            tvPaymentStatus.text = "Trạng thái: Chưa thanh toán"
        }

        order.OrderItems?.let {
            rvItems.adapter = OrderItemAdapter(it)
        }
    }

    private fun updateStatus(id: Long, status: String) {
        val body = HashMap<String, String>()
        body.put("status", status)
        RetrofitClient.orderApi.updateOrderStatus(id, body).enqueue(object : Callback<Order> {
            override fun onResponse(call: Call<Order>, response: Response<Order>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    loadOrderDetail()
                }
            }
            override fun onFailure(call: Call<Order>, t: Throwable) {
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadQR(id: Long) {
        ivQR.setImageResource(android.R.drawable.stat_sys_download)
        RetrofitClient.payosApi.createPaymentLink(mapOf("orderId" to id)).enqueue(object : Callback<com.example.appdatmon.data.api.PayosResponse> {
            override fun onResponse(call: Call<com.example.appdatmon.data.api.PayosResponse>, response: Response<com.example.appdatmon.data.api.PayosResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val checkoutUrl = body?.checkoutUrl
                    val qrContent = body?.qrCode ?: checkoutUrl
                    
                    if (qrContent != null) {
                        val encodedData = java.net.URLEncoder.encode(qrContent, "UTF-8")
                        val qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&data=$encodedData"
                        com.bumptech.glide.Glide.with(this@OrderDetailFragment)
                            .load(qrImageUrl)
                            .into(ivQR)
                            
                        ivQR.setOnClickListener {
                            if (checkoutUrl != null) {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(checkoutUrl))
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
            override fun onFailure(call: Call<com.example.appdatmon.data.api.PayosResponse>, t: Throwable) {
                Toast.makeText(context, "Lỗi tạo QR PayOS", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startAutoStatusCheck(orderId: Long) {
        stopStatusCheck()
        isCheckingStatus = true
        checkStatusRunnable = object : Runnable {
            override fun run() {
                if (!isCheckingStatus) return
                
                android.util.Log.d("PayOS_Polling", "Đang kiểm tra đơn hàng #$orderId...")

                RetrofitClient.payosApi.checkOrderStatus(orderId).enqueue(object : Callback<com.example.appdatmon.data.api.OrderStatusResponse> {
                    override fun onResponse(call: Call<com.example.appdatmon.data.api.OrderStatusResponse>, response: Response<com.example.appdatmon.data.api.OrderStatusResponse>) {
                        val currentStatus = response.body()?.status
                        if (response.isSuccessful && currentStatus == "PAID") {
                            isCheckingStatus = false
                            Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_LONG).show()
                            loadOrderDetail() 
                            handler.postDelayed({
                                if (isAdded) parentFragmentManager.popBackStack()
                            }, 1500)
                        } else if (isCheckingStatus) {
                            checkStatusRunnable?.let { handler.postDelayed(it, 3000) }
                        }
                    }
                    override fun onFailure(call: Call<com.example.appdatmon.data.api.OrderStatusResponse>, t: Throwable) {
                        if (isCheckingStatus) checkStatusRunnable?.let { handler.postDelayed(it, 5000) }
                    }
                })
            }
        }
        handler.post(checkStatusRunnable!!)
    }

    private fun stopStatusCheck() {
        isCheckingStatus = false
        checkStatusRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun performPayment(orderId: Long, method: String) {
        val paymentData = mapOf("paymentMethod" to method)
        RetrofitClient.orderApi.payOrder(orderId, paymentData).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
                    loadOrderDetail() 
                } else {
                    Toast.makeText(context, "Thanh toán thất bại", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(context, "Lỗi kết nối khi thanh toán", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopStatusCheck()
    }
}
