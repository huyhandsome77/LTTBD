package com.example.appdatmon

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
    private val allTables = mutableListOf<RestaurantTable>()
    private val filteredTables = mutableListOf<RestaurantTable>()

    private lateinit var btnFilterAll: Button
    private lateinit var btnFilterUsing: Button
    private lateinit var btnFilterEmpty: Button

    private var isCheckingStatus = false
    private val handler = Handler(Looper.getMainLooper())
    private var checkStatusRunnable: Runnable? = null
    private var currentFilterType = "ALL"
    private var lastClickTime: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quanlyban, container, false)

        btnFilterAll = view.findViewById(R.id.btnFilterAll)
        btnFilterUsing = view.findViewById(R.id.btnFilterUsing)
        btnFilterEmpty = view.findViewById(R.id.btnFilterEmpty)

        val rvTables = view.findViewById<RecyclerView>(R.id.rvTables)
        rvTables.layoutManager = GridLayoutManager(context, 3)
        
        adapter = TableAdapter(filteredTables) { table ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > 1000) { // Debounce 1 giây
                lastClickTime = currentTime
                showTableDetailDialog(table)
            }
        }
        rvTables.adapter = adapter

        setupFilters()
        loadTables()

        return view
    }

    private fun setupFilters() {
        btnFilterAll.setOnClickListener {
            currentFilterType = "ALL"
            updateFilterButtons(btnFilterAll)
            filterTables("ALL")
        }
        btnFilterUsing.setOnClickListener {
            currentFilterType = "USING"
            updateFilterButtons(btnFilterUsing)
            filterTables("USING")
        }
        btnFilterEmpty.setOnClickListener {
            currentFilterType = "EMPTY"
            updateFilterButtons(btnFilterEmpty)
            filterTables("EMPTY")
        }
    }

    private fun updateFilterButtons(activeButton: Button) {
        val inactiveColor = ColorStateList.valueOf(Color.parseColor("#ECEFF1"))
        val activeColor = ColorStateList.valueOf(resources.getColor(R.color.admin_primary, null))
        val textInactive = Color.parseColor("#555555")
        val textActive = Color.WHITE
        
        btnFilterAll.backgroundTintList = inactiveColor
        btnFilterAll.setTextColor(textInactive)
        
        btnFilterUsing.backgroundTintList = inactiveColor
        btnFilterUsing.setTextColor(textInactive)
        
        btnFilterEmpty.backgroundTintList = inactiveColor
        btnFilterEmpty.setTextColor(textInactive)
        
        activeButton.backgroundTintList = activeColor
        activeButton.setTextColor(textActive)
    }

    private fun filterTables(filterType: String) {
        filteredTables.clear()
        when (filterType) {
            "ALL" -> filteredTables.addAll(allTables)
            "USING" -> filteredTables.addAll(allTables.filter { it.status == "OCCUPIED" || it.status == "BOOKED" })
            "EMPTY" -> filteredTables.addAll(allTables.filter { it.status == "AVAILABLE" || it.status == "CLEANING" })
        }
        adapter.updateData(filteredTables)
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
                    allTables.clear()
                    allTables.addAll(tables)
                    filterTables(currentFilterType)
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
        
        dialog.setOnDismissListener { stopStatusCheck() }

        val tvTitle = dialog.findViewById<TextView>(R.id.tvDialogTitle)
        val tvNoOrder = dialog.findViewById<TextView>(R.id.tvNoOrder)
        val layoutOrderInfo = dialog.findViewById<View>(R.id.layoutOrderInfo)
        val rvOrderItems = dialog.findViewById<RecyclerView>(R.id.rvOrderItems)
        val tvTotalAmount = dialog.findViewById<TextView>(R.id.tvTotalAmount)
        val tvOriginalAmount = dialog.findViewById<TextView>(R.id.tvOriginalAmount)
        val tvDiscountAmount = dialog.findViewById<TextView>(R.id.tvDiscountAmount)
        val btnPay = dialog.findViewById<Button>(R.id.btnPay)
        val btnClose = dialog.findViewById<Button>(R.id.btnCloseDialog)
        val rgPaymentMethod = dialog.findViewById<android.widget.RadioGroup>(R.id.rgPaymentMethod)
        val layoutQR = dialog.findViewById<View>(R.id.layoutQR)
        val ivQR = dialog.findViewById<android.widget.ImageView>(R.id.ivPaymentQR)

        tvTitle.text = "Chi tiết bàn B${String.format("%02d", table.number)}"
        rvOrderItems.layoutManager = LinearLayoutManager(context)

        // HIỂN THỊ BILL NẾU BÀN KHÔNG TRỐNG
        if (!table.status.equals("AVAILABLE", ignoreCase = true) && 
            !table.status.equals("CLEANING", ignoreCase = true) &&
            !table.status.equals("BOOKED", ignoreCase = true)) {
            
            tvNoOrder.visibility = View.GONE
            layoutOrderInfo.visibility = View.VISIBLE

            RetrofitClient.orderApi.getCurrentOrderByTable(table.id!!).enqueue(object : Callback<List<Order>> {
                override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val orders = response.body()!!
                        
                        if (orders.isEmpty()) {
                            tvNoOrder.text = "Bàn đang dùng nhưng chưa có món nào trong đơn hàng"
                            tvNoOrder.visibility = View.VISIBLE
                            layoutOrderInfo.visibility = View.GONE
                            btnPay.visibility = View.GONE
                            return
                        }

                        // Gộp tất cả OrderItems từ các đơn hàng
                        val allItems = orders.flatMap { it.OrderItems ?: emptyList() }
                        rvOrderItems.adapter = OrderItemAdapter(allItems)
                        
                        // Tính tổng các loại giá
                        val totalOriginal = orders.sumOf { it.totalPrice }
                        val totalDiscount = orders.sumOf { it.discountAmount }
                        val totalFinal = orders.sumOf { it.finalPrice }
                        
                        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                        tvOriginalAmount.text = formatter.format(totalOriginal)
                        tvDiscountAmount.text = "-${formatter.format(totalDiscount)}"
                        tvTotalAmount.text = formatter.format(totalFinal)

                        // Hiển thị nút thanh toán
                        btnPay.visibility = View.VISIBLE
                        rgPaymentMethod.visibility = View.VISIBLE
                        
                        rgPaymentMethod.setOnCheckedChangeListener { _, checkedId ->
                            if (checkedId == R.id.rbTransfer) {
                                layoutQR.visibility = View.VISIBLE
                                btnPay.text = "XÁC NHẬN ĐÃ NHẬN TIỀN"
                                
                                val firstOrderId = orders.firstOrNull()?.id
                                if (firstOrderId != null) {
                                    startAutoStatusCheck(firstOrderId, dialog)
                                    val params = mapOf(
                                        "tableId" to table.id!!
                                    )
                                    RetrofitClient.payosApi.createPaymentLink(params).enqueue(object : Callback<com.example.appdatmon.data.api.PayosResponse> {
                                        override fun onResponse(call: Call<com.example.appdatmon.data.api.PayosResponse>, response: Response<com.example.appdatmon.data.api.PayosResponse>) {
                                            if (response.isSuccessful && response.body() != null) {
                                                val body = response.body()
                                                val qrContent = body?.qrCode ?: body?.checkoutUrl
                                                if (qrContent != null) {
                                                    val encodedData = java.net.URLEncoder.encode(qrContent, "UTF-8")
                                                    val qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&data=$encodedData"
                                                    com.bumptech.glide.Glide.with(requireContext()).load(qrImageUrl).into(ivQR)
                                                }
                                            }
                                        }
                                        override fun onFailure(call: Call<com.example.appdatmon.data.api.PayosResponse>, t: Throwable) {}
                                    })
                                }
                            } else {
                                layoutQR.visibility = View.GONE
                                btnPay.text = "THANH TOÁN TIỀN MẶT"
                                stopStatusCheck()
                            }
                        }

                        btnPay.setOnClickListener {
                            val method = if (rgPaymentMethod.checkedRadioButtonId == R.id.rbTransfer) "TRANSFER" else "CASH"
                            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Xác nhận thanh toán")
                                .setMessage("Xác nhận thanh toán gộp cho tất cả đơn hàng tại bàn này?")
                                .setPositiveButton("Xác nhận") { _, _ ->
                                    performBulkPayment(table.id!!, method, dialog)
                                }
                                .setNegativeButton("Hủy", null)
                                .show()
                        }
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                        Log.e("QuanLyBanFragment", "Server returned error: $errorMsg")
                        tvNoOrder.text = "Bàn đang sử dụng nhưng chưa có hóa đơn (404)"
                        tvNoOrder.visibility = View.VISIBLE
                        layoutOrderInfo.visibility = View.GONE
                        btnPay.visibility = View.GONE
                    }
                }
                override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                    Log.e("QuanLyBanFragment", "Network or Parsing error: ${t.message}", t)
                    tvNoOrder.text = "Lỗi kết nối hoặc dữ liệu: ${t.localizedMessage}"
                    tvNoOrder.visibility = View.VISIBLE
                    layoutOrderInfo.visibility = View.GONE
                    btnPay.visibility = View.GONE
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

    private fun startAutoStatusCheck(orderId: Long, dialog: Dialog) {
        stopStatusCheck()
        isCheckingStatus = true
        checkStatusRunnable = object : Runnable {
            override fun run() {
                if (!isCheckingStatus) return
                RetrofitClient.payosApi.checkOrderStatus(orderId).enqueue(object : Callback<com.example.appdatmon.data.api.OrderStatusResponse> {
                    override fun onResponse(call: Call<com.example.appdatmon.data.api.OrderStatusResponse>, response: Response<com.example.appdatmon.data.api.OrderStatusResponse>) {
                        val currentStatus = response.body()?.status
                        if (response.isSuccessful && currentStatus == "PAID") {
                            isCheckingStatus = false
                            Toast.makeText(context, "Khách đã thanh toán thành công!", Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                            loadTables()
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

    private fun performBulkPayment(tableId: Long, method: String, dialog: Dialog) {
        val paymentData = mapOf("paymentMethod" to method)
        RetrofitClient.orderApi.payAllOrdersByTable(tableId, paymentData).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Thanh toán thành công tất cả đơn hàng!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    loadTables()
                } else {
                    Toast.makeText(context, "Thanh toán thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(context, "Lỗi kết nối khi thanh toán", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun performPayment(orderId: Long, method: String, dialog: Dialog) {
        val paymentData = mapOf("paymentMethod" to method)
        RetrofitClient.orderApi.payOrder(orderId, paymentData).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    loadTables()
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
