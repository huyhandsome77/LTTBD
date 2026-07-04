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
import com.example.appdatmon.data.model.Reservation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class BookingHistoryActivity : AppCompatActivity() {

    private lateinit var rvBookingHistory: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: UserBookingAdapter
    private var bookingList = mutableListOf<Reservation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_history)

        rvBookingHistory = findViewById(R.id.rvBookingHistory)
        swipeRefresh = findViewById(R.id.swipeRefreshBookingHistory)

        rvBookingHistory.layoutManager = LinearLayoutManager(this)
        adapter = UserBookingAdapter(bookingList)
        rvBookingHistory.adapter = adapter

        findViewById<ImageView>(R.id.btnBackBookingHistory).setOnClickListener {
            finish()
        }

        swipeRefresh.setOnRefreshListener {
            loadBookingHistory()
        }

        loadBookingHistory()
    }

    private fun loadBookingHistory() {
        swipeRefresh.isRefreshing = true
        RetrofitClient.reservationApi.getMyReservations().enqueue(object : Callback<List<Reservation>> {
            override fun onResponse(call: Call<List<Reservation>>, response: Response<List<Reservation>>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful && response.body() != null) {
                    bookingList.clear()
                    bookingList.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<Reservation>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Toast.makeText(this@BookingHistoryActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

class UserBookingAdapter(private val list: List<Reservation>) : RecyclerView.Adapter<UserBookingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTable: TextView = view.findViewById(R.id.tvBookingTable)
        val tvStatus: TextView = view.findViewById(R.id.tvBookingStatus)
        val tvTime: TextView = view.findViewById(R.id.tvBookingTime)
        val tvGuests: TextView = view.findViewById(R.id.tvBookingGuests)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvTable.text = "Bàn số: ${item.table?.tableNumber ?: "Đang chờ xếp"}"
        holder.tvGuests.text = "Số khách: ${item.numberOfGuests} người"

        holder.tvStatus.text = when (item.status) {
            "PENDING" -> "Chờ duyệt"
            "CONFIRMED" -> "Đã xác nhận"
            "CHECKED_IN" -> "Đã nhận bàn"
            "CANCELLED" -> "Đã hủy"
            else -> item.status
        }

        // Format time
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(item.reservationTime)
            holder.tvTime.text = outputFormat.format(date!!)
        } catch (e: Exception) {
            holder.tvTime.text = item.reservationTime
        }
    }

    override fun getItemCount() = list.size
}
