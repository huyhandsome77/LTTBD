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
            showTableDetailDialog(table)
        }
        rvTables.adapter = adapter

        setupFilters()
        loadTables()

        return view
    }

    private var currentFilterType = "ALL"

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
                    filterTables(currentFilterType) // Sử dụng filter hiện tại thay vì mặc định ALL
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
        val btnPay = dialog.findViewById<Button>(R.id.btnPay)
        val btnClose = dialog.findViewById<Button>(R.id.btnCloseDialog)
        val rgPaymentMethod = dialog.findViewById<android.widget.RadioGroup>(R.id.rgPaymentMethod)
        val layoutQR = dialog.findViewById<View>(R.id.layoutQR)
        val ivQR = dialog.findViewById<android.widget.ImageView>(R.id.ivPaymentQR)

        tvTitle.text = "Chi tiết bàn B${String.format("%02d", table.number)}"
        rvOrderItems.layoutManager = LinearLayoutManager(context)

        if (table.status == "OCCUPIED") {
            tvNoOrder.visibility = View.GONE
            layoutOrderInfo.visibility = View.VISIBLE
            btnPay.visibility = View.VISIBLE

            RetrofitClient.orderApi.getCurrentOrderByTable(table.id!!).enqueue(object : Callback<Order> {
                override fun onResponse(call: Call<Order>, response: Response<Order>) {
                    if (response.isSuccessful && response.body() != null) {
                        val order = response.body()!!
                        val orderItems = order.OrderItems ?: emptyList()
                        rvOrderItems.adapter = OrderItemAdapter(orderItems)
                        
                        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                        tvTotalAmount.text = formatter.format(order.finalPrice)

                        rgPaymentMethod.setOnCheckedChangeListener { _, checkedId ->
                            if (checkedId == R.id.rbTransfer) {
                                layoutQR.visibility = View.VISIBLE
                                btnPay.visibility = View.VISIBLE // Hiện nút xác nhận thủ công
                                btnPay.text = "XÁC NHẬN ĐÃ NHẬN TIỀN"
                                startAutoStatusCheck(order.id, dialog)
                                
                                ivQR.setImageResource(android.R.drawable.stat_sys_download)
                                RetrofitClient.payosApi.createPaymentLink(mapOf("orderId" to order.id)).enqueue(object : Callback<com.example.appdatmon.data.api.PayosResponse> {
                                    override fun onResponse(call: Call<com.example.appdatmon.data.api.PayosResponse>, response: Response<com.example.appdatmon.data.api.PayosResponse>) {
                                        if (response.isSuccessful && response.body() != null) {
                                            val body = response.body()
                                            val checkoutUrl = body?.checkoutUrl
                                            val qrContent = body?.qrCode ?: checkoutUrl
                                            if (qrContent != null) {
                                                val encodedData = java.net.URLEncoder.encode(qrContent, "UTF-8")
                                                val qrImageUrl = "https://api.qrserver.com/v1/create-qr-code/?size=400x400&data=$encodedData"
                                                com.bumptech.glide.Glide.with(requireContext()).load(qrImageUrl).into(ivQR)
                                                ivQR.setOnClickListener {
                                                    if (checkoutUrl != null) {
                                                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(checkoutUrl))
                                                        startActivity(intent)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    override fun onFailure(call: Call<com.example.appdatmon.data.api.PayosResponse>, t: Throwable) {}
                                })
                            } else {
                                layoutQR.visibility = View.GONE
                                btnPay.visibility = View.VISIBLE
                                btnPay.text = "THANH TOÁN TIỀN MẶT"
                                stopStatusCheck()
                            }
                        }

                        btnPay.setOnClickListener {
                            val method = if (rgPaymentMethod.checkedRadioButtonId == R.id.rbTransfer) "TRANSFER" else "CASH"
                            // Xác nhận xác nhận thủ công
                            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Xác nhận thanh toán")
                                .setMessage("Bạn có chắc chắn muốn xác nhận thanh toán cho đơn hàng này?")
                                .setPositiveButton("Xác nhận") { _, _ ->
                                    performPayment(order.id, method, dialog)
                                }
                                .setNegativeButton("Hủy", null)
                                .show()
                        }
                    } else {
                        tvNoOrder.text = "Không tìm thấy hóa đơn hoạt động"
                        tvNoOrder.visibility = View.VISIBLE
                        layoutOrderInfo.visibility = View.GONE
                        btnPay.visibility = View.GONE
                    }
                }
                override fun onFailure(call: Call<Order>, t: Throwable) {}
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
                
                Log.d("PayOS_Polling", "Đang kiểm tra trạng thái đơn hàng #$orderId...")
                
                RetrofitClient.payosApi.checkOrderStatus(orderId).enqueue(object : Callback<com.example.appdatmon.data.api.OrderStatusResponse> {
                    override fun onResponse(call: Call<com.example.appdatmon.data.api.OrderStatusResponse>, response: Response<com.example.appdatmon.data.api.OrderStatusResponse>) {
                        val currentStatus = response.body()?.status
                        Log.d("PayOS_Polling", "Trạng thái hiện tại: $currentStatus")

                        if (response.isSuccessful && currentStatus == "PAID") {
                            isCheckingStatus = false
                            Toast.makeText(context, "Khách đã thanh toán thành công!", Toast.LENGTH_LONG).show()
                            dialog.dismiss()
                            loadTables() // Tải lại danh sách bàn
                        } else if (isCheckingStatus) {
                            checkStatusRunnable?.let { handler.postDelayed(it, 3000) }
                        }
                    }
                    override fun onFailure(call: Call<com.example.appdatmon.data.api.OrderStatusResponse>, t: Throwable) {
                        Log.e("PayOS_Polling", "Lỗi kiểm tra: ${t.message}")
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
