package com.example.appdatmon.ui.kitchen

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.api.AuthManager
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.KitchenItem
import com.example.appdatmon.ui.auth.LoginActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KitchenActivity : AppCompatActivity() {

    private lateinit var rvKitchenItems: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var adapter: KitchenAdapter
    private var kitchenItemList = ArrayList<KitchenItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitchen)

        rvKitchenItems = findViewById(R.id.rvKitchenItems)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        val btnRefresh = findViewById<ImageView>(R.id.btnRefresh)
        val btnLogout = findViewById<ImageView>(R.id.btnLogout)

        // Setup RecyclerView
        rvKitchenItems.layoutManager = LinearLayoutManager(this)
        adapter = KitchenAdapter(kitchenItemList) { item, nextStatus ->
            updateItemStatus(item, nextStatus)
        }
        rvKitchenItems.adapter = adapter

        // Setup Top Bar Actions
        btnRefresh.setOnClickListener {
            fetchKitchenItems()
        }

        btnLogout.setOnClickListener {
            AuthManager.clear(this)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Initial Load
        fetchKitchenItems()
    }

    private fun fetchKitchenItems() {
        RetrofitClient.kitchenApi.getKitchenItems().enqueue(object : Callback<List<KitchenItem>> {
            override fun onResponse(call: Call<List<KitchenItem>>, response: Response<List<KitchenItem>>) {
                if (response.isSuccessful) {
                    val list = response.body()
                    kitchenItemList.clear()
                    if (list != null) {
                        kitchenItemList.addAll(list)
                    }
                    
                    adapter.updateData(kitchenItemList)
                    
                    if (kitchenItemList.isEmpty()) {
                        tvEmptyState.visibility = View.VISIBLE
                        rvKitchenItems.visibility = View.GONE
                    } else {
                        tvEmptyState.visibility = View.GONE
                        rvKitchenItems.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this@KitchenActivity, "Lấy danh sách thất bại: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<KitchenItem>>, t: Throwable) {
                Toast.makeText(this@KitchenActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateItemStatus(item: KitchenItem, nextStatus: String) {
        val body = mapOf("status" to nextStatus)
        RetrofitClient.kitchenApi.updateStatus(item.id, body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val statusText = if (nextStatus == "COOKING") "bắt đầu nấu" else "hoàn thành"
                    Toast.makeText(
                        this@KitchenActivity, 
                        "Món ${item.Product?.name ?: ""} đã $statusText", 
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchKitchenItems()
                } else {
                    Toast.makeText(this@KitchenActivity, "Cập nhật thất bại: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@KitchenActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
