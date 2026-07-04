package com.example.appdatmon.ui.User

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.appdatmon.R
import com.example.appdatmon.data.api.AuthManager
import com.example.appdatmon.data.api.CreateReservationRequest
import com.example.appdatmon.data.api.CreateReservationResponse
import com.example.appdatmon.data.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var etGuestName: EditText
    private lateinit var etGuestPhone: EditText
    private lateinit var etGuestCount: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private lateinit var btnSubmit: Button
    private lateinit var timeLayout: LinearLayout
    
    private var selectedDate: Calendar = Calendar.getInstance()
    private var isTimeSelected = false
    private var selectedHour: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        initViews()
        setupDateTimePickers()

        findViewById<ImageView>(R.id.btnBackBooking).setOnClickListener {
            finish()
        }

        btnSubmit.setOnClickListener {
            submitBooking()
        }
    }

    private fun initViews() {
        etGuestName = findViewById(R.id.etGuestName)
        etGuestPhone = findViewById(R.id.etGuestPhone)
        etGuestCount = findViewById(R.id.etGuestCount)
        etDate = findViewById(R.id.etDate)
        etTime = findViewById(R.id.etTime)
        btnSubmit = findViewById(R.id.btnSubmitBooking)
        timeLayout = findViewById(R.id.timeLayout)
    }

    private fun setupDateTimePickers() {
        val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // DATE PICKER
        etDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    selectedDate.set(year, month, day)
                    etDate.setText(sdfDate.format(selectedDate.time))
                    // Khi đổi ngày, reset giờ
                    resetTimeSelection()
                    setupTimeChips()
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        }
        
        // Mặc định ngày hôm nay và load chips
        etDate.setText(sdfDate.format(selectedDate.time))
        setupTimeChips()
    }

    private fun resetTimeSelection() {
        etTime.setText("")
        isTimeSelected = false
        selectedHour = -1
    }

    private fun setupTimeChips() {
        timeLayout.removeAllViews()
        
        val now = Calendar.getInstance()
        val isToday = selectedDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                selectedDate.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
        val currentHour = now.get(Calendar.HOUR_OF_DAY)

        for (hour in 6..22) {
            // Nếu là hôm nay, chỉ hiện các giờ trong tương lai
            if (isToday && hour <= currentHour) continue

            val chip = TextView(this)
            val params = LinearLayout.LayoutParams(
                dpToPx(90),
                dpToPx(42)
            )
            params.setMargins(0, 0, dpToPx(12), 0)
            chip.layoutParams = params
            
            chip.gravity = Gravity.CENTER
            chip.text = "${hour}h"
            chip.setTextColor(Color.WHITE)
            
            // Set background dựa trên việc có đang được chọn hay không
            if (hour == selectedHour) {
                chip.setBackgroundResource(R.drawable.bg_category_active)
            } else {
                chip.setBackgroundResource(R.drawable.bg_category)
            }

            chip.setOnClickListener {
                updateSelectedHour(hour)
            }

            timeLayout.addView(chip)
        }
    }

    private fun updateSelectedHour(hour: Int) {
        selectedHour = hour
        selectedDate.set(Calendar.HOUR_OF_DAY, hour)
        selectedDate.set(Calendar.MINUTE, 0)
        selectedDate.set(Calendar.SECOND, 0)
        
        etTime.setText("${hour}h:00")
        isTimeSelected = true
        
        // Refresh giao diện chips để đổi màu
        setupTimeChips()
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun submitBooking() {
        val name = etGuestName.text.toString().trim()
        val phone = etGuestPhone.text.toString().trim()
        val countStr = etGuestCount.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || countStr.isEmpty() || !isTimeSelected) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin và chọn giờ đặt", Toast.LENGTH_SHORT).show()
            return
        }

        val count = try { countStr.toInt() } catch(e: Exception) { 1 }
        
        val isoSdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        isoSdf.timeZone = TimeZone.getTimeZone("UTC")
        val reservationTimeString = isoSdf.format(selectedDate.time)

        val request = CreateReservationRequest(
            guestName = name,
            guestPhone = phone,
            reservationTime = reservationTimeString,
            numberOfGuests = count,
            user_id = AuthManager.getUserId(this).takeIf { it != -1L }
        )

        RetrofitClient.reservationApi.createReservation(request).enqueue(object : Callback<CreateReservationResponse> {
            override fun onResponse(call: Call<CreateReservationResponse>, response: Response<CreateReservationResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    Toast.makeText(this@BookingActivity, "Đặt bàn thành công! Số bàn: ${data?.tableNumber}", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Hết bàn trống"
                    Toast.makeText(this@BookingActivity, "Lỗi: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<CreateReservationResponse>, t: Throwable) {
                Toast.makeText(this@BookingActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
