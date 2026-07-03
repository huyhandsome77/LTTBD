package com.example.appdatmon.ui.admin

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.appdatmon.R
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.api.StatResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AdminStatsFragment : Fragment() {

    private lateinit var tvSelectedDate: TextView
    private lateinit var tvTotalOrders: TextView
    private lateinit var tvTotalRevenue: TextView
    private lateinit var rgStatType: RadioGroup
    private lateinit var btnSelectDate: LinearLayout

    private var calendar = Calendar.getInstance()
    private var selectedType = "day"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_admin_stats, container, false)

        tvSelectedDate = view.findViewById(R.id.tvSelectedDate)
        tvTotalOrders = view.findViewById(R.id.tvTotalOrders)
        tvTotalRevenue = view.findViewById(R.id.tvTotalRevenue)
        rgStatType = view.findViewById(R.id.rgStatType)
        btnSelectDate = view.findViewById(R.id.btnSelectDate)

        updateDateDisplay()

        rgStatType.setOnCheckedChangeListener { _, checkedId ->
            selectedType = when (checkedId) {
                R.id.rbDay -> "day"
                R.id.rbMonth -> "month"
                R.id.rbYear -> "year"
                else -> "day"
            }
            updateDateDisplay()
            fetchStats()
        }

        btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        fetchStats()

        return view
    }

    private fun showDatePicker() {
        val dpd = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateDisplay()
            fetchStats()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        dpd.show()
    }

    private fun updateDateDisplay() {
        val format = when (selectedType) {
            "day" -> "dd/MM/yyyy"
            "month" -> "MM/yyyy"
            "year" -> "yyyy"
            else -> "dd/MM/yyyy"
        }
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        tvSelectedDate.text = sdf.format(calendar.time)
    }

    private fun fetchStats() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateParam = sdf.format(calendar.time)

        RetrofitClient.statApi.getStats(selectedType, dateParam).enqueue(object : Callback<StatResponse> {
            override fun onResponse(call: Call<StatResponse>, response: Response<StatResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    tvTotalOrders.text = (body?.totalOrders ?: 0).toString()
                    
                    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                    tvTotalRevenue.text = formatter.format(body?.totalRevenue ?: 0.0)
                } else {
                    Toast.makeText(context, "Lỗi tải thống kê", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StatResponse>, t: Throwable) {
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
