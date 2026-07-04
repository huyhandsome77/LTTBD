package com.example.appdatmon.ui.User

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.appdatmon.R
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.Order
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class OrderHistoryActivity : AppCompatActivity() {

    private lateinit var rvOrderHistory: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: UserOrderAdapter
    private var orderList = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        rvOrderHistory = findViewById(R.id.rvOrderHistory)
        swipeRefresh = findViewById(R.id.swipeRefreshOrderHistory)

        rvOrderHistory.layoutManager = LinearLayoutManager(this)
        adapter = UserOrderAdapter(orderList)
        rvOrderHistory.adapter = adapter

        findViewById<ImageView>(R.id.btnBackOrderHistory).setOnClickListener {
            finish()
        }

        swipeRefresh.setOnRefreshListener {
            loadOrderHistory()
        }

        loadOrderHistory()
    }

    private fun loadOrderHistory() {
        swipeRefresh.isRefreshing = true
        RetrofitClient.orderApi.getMyOrders().enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful && response.body() != null) {
                    orderList.clear()
                    orderList.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@OrderHistoryActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

class UserOrderAdapter(private val list: List<Order>) : RecyclerView.Adapter<UserOrderAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvOrderId)
        val tvStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvTable: TextView = view.findViewById(R.id.tvTableInfo)
        val tvTotal: TextView = view.findViewById(R.id.tvTotalAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvId.text = "#DH${item.id}"
        holder.tvTable.text = "Bàn: ${item.RestaurantTable?.tableNumber ?: "Mang về"}"
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        holder.tvTotal.text = formatter.format(item.finalPrice)

        holder.tvStatus.text = when (item.status) {
            "PENDING" -> "Chờ xác nhận"
            "CONFIRMED" -> "Đã xác nhận"
            "COMPLETED" -> "Hoàn thành"
            "CANCELLED" -> "Đã hủy"
            else -> item.status
        }

        // Format date
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(item.created_at)
            holder.tvDate.text = outputFormat.format(date!!)
        } catch (e: Exception) {
            holder.tvDate.text = item.created_at
        }
    }

    override fun getItemCount() = list.size
}
