package com.example.appdatmon.ui.User

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.api.*
import com.example.appdatmon.ui.User.adapter.UserCartAdapter
import com.example.appdatmon.utils.CartManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class CartActivity : AppCompatActivity() {

    private var tableId: Long = -1L
    private var tableNumber: Int = -1

    private lateinit var rvCartItems: RecyclerView
    private lateinit var adapter: UserCartAdapter
    private lateinit var tvTotalItems: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvUserPoints: TextView
    private lateinit var btnApplyPoints: TextView
    private lateinit var btnOrder: Button

    private var userPoints: Int = 0
    private var isPointsApplied: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initViews()
        setupAdapter()
        setupListeners()
        loadData()
    }

    private fun initViews() {
        rvCartItems = findViewById(R.id.rvCartItems)
        tvTotalItems = findViewById(R.id.tvTotalItemsCart)
        tvTotalPrice = findViewById(R.id.tvTotalPriceCart)
        tvUserPoints = findViewById(R.id.tvUserPointsCart)
        btnApplyPoints = findViewById(R.id.btnApplyPoints)
        btnOrder = findViewById(R.id.btnOrder)

        rvCartItems.layoutManager = LinearLayoutManager(this)
    }

    private fun setupAdapter() {
        adapter = UserCartAdapter(
            CartManager.getItems(),
            onDeleteClick = { item ->
                CartManager.removeProduct(item.product.id!!)
                updateSummary()
                adapter.updateData(CartManager.getItems())
            },
            onQuantityChange = { item, newQty ->
                CartManager.updateQuantity(item.product.id!!, newQty)
                updateSummary()
                adapter.updateData(CartManager.getItems())
            }
        )
        rvCartItems.adapter = adapter
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.btnBackCart).setOnClickListener {
            finish()
        }

        btnOrder.setOnClickListener {
            placeOrder()
        }

        btnApplyPoints.setOnClickListener {
            if (userPoints > 0) {
                isPointsApplied = !isPointsApplied
                if (isPointsApplied) {
                    btnApplyPoints.text = "HỦY"
                    btnApplyPoints.setBackgroundResource(R.drawable.bg_delete)
                    Toast.makeText(this, "Đã áp dụng điểm thưởng", Toast.LENGTH_SHORT).show()
                } else {
                    btnApplyPoints.text = "ÁP DỤNG"
                    btnApplyPoints.setBackgroundResource(R.drawable.bg_category_active)
                    Toast.makeText(this, "Đã hủy áp dụng điểm", Toast.LENGTH_SHORT).show()
                }
                updateSummary()
            } else {
                Toast.makeText(this, "Bạn không có điểm để áp dụng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadData() {
        // NHẬN DỮ LIỆU BÀN
        tableId = intent.getLongExtra("TABLE_ID", -1L)
        tableNumber = intent.getIntExtra("TABLE_NUMBER", -1)

        val tvTableInfo = findViewById<TextView>(R.id.tvCartTableInfo)
        if (tableId != -1L) {
            tvTableInfo.visibility = View.VISIBLE
            tvTableInfo.text = if (tableNumber != -1) "Bàn số: $tableNumber" else "Bàn đã chọn"
        }

        updateSummary()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        if (AuthManager.getToken(this) != null) {
            RetrofitClient.userApi.getUserProfile().enqueue(object : Callback<com.example.appdatmon.data.model.User> {
                override fun onResponse(call: Call<com.example.appdatmon.data.model.User>, response: Response<com.example.appdatmon.data.model.User>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        userPoints = user?.points ?: 0
                        tvUserPoints.text = "Bạn có $userPoints điểm"
                    }
                }
                override fun onFailure(call: Call<com.example.appdatmon.data.model.User>, t: Throwable) {}
            })
        }
    }

    private fun updateSummary() {
        val totalItems = CartManager.getCount()
        var totalPrice = CartManager.getTotalPrice()

        if (isPointsApplied) {
            val discount = userPoints.toDouble() // 1 điểm = 1đ
            totalPrice -= discount
            if (totalPrice < 0) totalPrice = 0.0
        }

        tvTotalItems.text = totalItems.toString()
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        tvTotalPrice.text = formatter.format(totalPrice)
        
        btnOrder.isEnabled = totalItems > 0
    }

    private fun placeOrder() {
        val cartItems = CartManager.getItems()
        if (cartItems.isEmpty()) return

        val itemsRequest = cartItems.map { 
            OrderItemRequest(product_id = it.product.id!!, quantity = it.quantity) 
        }

        val currentUserId = AuthManager.getUserId(this)

        val request = OrderRequest(
            table_id = if (tableId != -1L) tableId else null,
            user_id = if (currentUserId != -1L) currentUserId else null,
            items = itemsRequest,
            note = "Khách đặt từ App",
            used_points = if (isPointsApplied) userPoints else 0
        )

        btnOrder.isEnabled = false
        btnOrder.text = "ĐANG ĐẶT..."

        RetrofitClient.orderApi.createOrder(request).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                btnOrder.isEnabled = true
                btnOrder.text = "ĐẶT MÓN"
                
                if (response.isSuccessful) {
                    CartManager.clear()
                    Toast.makeText(this@CartActivity, "Đặt món thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CartActivity, "Lỗi đặt món: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                btnOrder.isEnabled = true
                btnOrder.text = "ĐẶT MÓN"
                Toast.makeText(this@CartActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
