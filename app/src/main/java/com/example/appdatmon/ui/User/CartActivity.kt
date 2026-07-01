package com.example.appdatmon

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appdatmon.data.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartActivity : AppCompatActivity() {

    private var tableId: Long = -1L
    private var tableQR: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // NHẬN DỮ LIỆU BÀN
        tableId = intent.getLongExtra("TABLE_ID", -1L)
        tableQR = intent.getStringExtra("TABLE_QR")

        val tvTableInfo = findViewById<TextView>(R.id.tvCartTableInfo)
        if (tableId != -1L) {
            tvTableInfo.visibility = View.VISIBLE
            val displayNum = tableQR?.replace("TABLE_", "") ?: "--"
            tvTableInfo.text = "Bàn số: $displayNum"
        }

        val btnBack = findViewById<ImageView>(R.id.btnBackCart)
        btnBack.setOnClickListener {
            finish()
        }

        val btnOrder = findViewById<Button>(R.id.btnOrder)
        btnOrder.setOnClickListener {
            placeOrder()
        }

    }

    private fun placeOrder() {
        // Giả sử ta lấy danh sách món ăn từ CartManager hoặc Adapter
        // Đây là ví dụ demo với dữ liệu cứng
        val items = listOf(
            OrderItemRequest(product_id = 1, quantity = 2, note = "Không cay"),
            OrderItemRequest(product_id = 2, quantity = 1)
        )

        val currentUserId = AuthManager.getUserId(this)

        val request = OrderRequest(
            table_id = if (tableId != -1L) tableId else null,
            user_id = if (currentUserId != -1L) currentUserId else null,
            items = items,
            note = "Khách đặt từ App"
        )

        RetrofitClient.orderApi.createOrder(request).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CartActivity, "Đặt món thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@CartActivity, "Lỗi đặt món: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Toast.makeText(this@CartActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
