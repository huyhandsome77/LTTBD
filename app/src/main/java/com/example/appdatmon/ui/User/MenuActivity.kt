package com.example.appdatmon

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.api.TableResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private var tableQR: String? = null
    private var tableId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menu)

        // NHẬN DỮ LIỆU QR
        tableQR = intent.getStringExtra("QR_CODE")
        val tvTableNumber = findViewById<TextView>(R.id.tvTableNumber)

        if (tableQR != null) {
            tvTableNumber.visibility = View.VISIBLE
            // Gọi API lấy thông tin bàn chi tiết từ mã QR
            fetchTableInfo(tableQR!!)
        }

        // BACK BUTTON

        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {

            finish()

        }

        // CART BUTTON

        val btnCart = findViewById<CardView>(R.id.btnCart)

        btnCart.setOnClickListener {

            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("TABLE_QR", tableQR)
            intent.putExtra("TABLE_ID", tableId)
            startActivity(intent)

        }

    }

    private fun fetchTableInfo(qrCode: String) {
        RetrofitClient.tableApi.getTableByQRCode(qrCode).enqueue(object : Callback<TableResponse> {
            override fun onResponse(call: Call<TableResponse>, response: Response<TableResponse>) {
                if (response.isSuccessful) {
                    val table = response.body()
                    tableId = table?.id
                    val tvTableNumber = findViewById<TextView>(R.id.tvTableNumber)
                    tvTableNumber.text = "Bàn số: ${table?.tableNumber ?: "--"}"
                }
            }

            override fun onFailure(call: Call<TableResponse>, t: Throwable) {
                // Xử lý lỗi nếu cần
            }
        })
    }

}
