package com.example.appdatmon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.Reservation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class ReservationListFragment : Fragment() {

    private lateinit var rvReservations: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: ReservationAdapter
    private var resList = mutableListOf<Reservation>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reservation_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvReservations = view.findViewById(R.id.rvReservations)
        swipeRefresh = view.findViewById(R.id.swipeRefreshRes)

        rvReservations.layoutManager = LinearLayoutManager(context)
        adapter = ReservationAdapter(resList,
            onCheckIn = { res -> confirmCheckIn(res) },
            onCancel = { res -> confirmCancel(res) }
        )
        rvReservations.adapter = adapter

        swipeRefresh.setOnRefreshListener { loadReservations() }

        loadReservations()
    }

    private fun loadReservations() {
        swipeRefresh.isRefreshing = true
        RetrofitClient.reservationApi.getAllReservations().enqueue(object : Callback<List<Reservation>> {
            override fun onResponse(call: Call<List<Reservation>>, response: Response<List<Reservation>>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful && response.body() != null) {
                    resList.clear()
                    resList.addAll(response.body()!!)
                    adapter.updateData(resList)
                } else {
                    Toast.makeText(context, "Lỗi tải lịch sử đặt bàn", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Reservation>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Log.e("ResListFragment", "onFailure: ${t.message}")
                Toast.makeText(context, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmCheckIn(res: Reservation) {
        // Kiểm tra thời gian: Chỉ cho phép nhận bàn trước/sau 30 phút so với giờ đặt
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val reservationDate = inputFormat.parse(res.reservationTime)

            if (reservationDate != null) {
                val currentTime = System.currentTimeMillis()
                val resTime = reservationDate.time
                val diff = abs(currentTime - resTime)
                val thirtyMinutesInMs = 30 * 60 * 1000

                if (diff > thirtyMinutesInMs) {
                    Toast.makeText(context, "Chỉ có thể nhận bàn trong khoảng 30 phút trước và sau giờ đặt", Toast.LENGTH_LONG).show()
                    return
                }
            }
        } catch (e: Exception) {
            Log.e("ResListFragment", "Lỗi kiểm tra thời gian: ${e.message}")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận nhận bàn")
            .setMessage("Khách '${res.guestName}' đã đến và nhận bàn ${res.table?.tableNumber}?")
            .setPositiveButton("Xác nhận") { _, _ ->
                performAction(RetrofitClient.reservationApi.checkIn(res.id), "Đã nhận bàn")
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun confirmCancel(res: Reservation) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hủy đặt bàn")
            .setMessage("Bạn có chắc chắn muốn hủy lịch đặt của '${res.guestName}'?")
            .setPositiveButton("Hủy đặt") { _, _ ->
                performAction(RetrofitClient.reservationApi.cancel(res.id), "Đã hủy đặt bàn")
            }
            .setNegativeButton("Quay lại", null)
            .show()
    }

    private fun performAction(call: Call<Map<String, String>>, successMsg: String) {
        call.enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
                    loadReservations()
                } else {
                    Toast.makeText(context, "Thao tác thất bại", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
