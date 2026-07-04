package com.example.appdatmon.ui.User

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.appdatmon.R
import com.example.appdatmon.data.api.AuthManager
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserPoints: TextView
    private lateinit var tvOrderCount: TextView
    private lateinit var tvBookingCount: TextView
    private lateinit var btnLogout: Button
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        setupListeners()
        loadProfileData()
    }

    private fun initViews() {
        tvUserName = findViewById(R.id.tvUserNameProfile)
        tvUserEmail = findViewById(R.id.tvUserEmailProfile)
        tvUserPoints = findViewById(R.id.tvUserPoints)
        tvOrderCount = findViewById(R.id.tvOrderCount)
        tvBookingCount = findViewById(R.id.tvBookingCount)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.btnBackProfile).setOnClickListener {
            finish()
        }

        findViewById<LinearLayout>(R.id.btnEditProfile).setOnClickListener {
            showEditProfileDialog()
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnOrderHistory).setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }

        findViewById<androidx.cardview.widget.CardView>(R.id.btnBookingHistory).setOnClickListener {
            startActivity(Intent(this, BookingHistoryActivity::class.java))
        }

        btnLogout.setOnClickListener {
            AuthManager.clear(this)
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loadProfileData() {
        if (AuthManager.getToken(this) == null) {
            btnLogout.visibility = View.GONE
            return
        }

        // 1. Load User Profile
        RetrofitClient.userApi.getUserProfile().enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    currentUser = user
                    tvUserName.text = user?.fullName ?: "Chưa cập nhật"
                    tvUserEmail.text = user?.email ?: user?.phone ?: "Chưa có email"
                    tvUserPoints.text = (user?.points ?: 0).toString()
                }
            }
            override fun onFailure(call: Call<User>, t: Throwable) {}
        })

        // 2. Load Order Count
        RetrofitClient.orderApi.getMyOrders().enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    val count = response.body()?.size ?: 0
                    tvOrderCount.text = "$count đơn hàng gần đây"
                }
            }
            override fun onFailure(call: Call<List<Order>>, t: Throwable) {}
        })

        // 3. Load Booking Count
        RetrofitClient.reservationApi.getMyReservations().enqueue(object : Callback<List<Reservation>> {
            override fun onResponse(call: Call<List<Reservation>>, response: Response<List<Reservation>>) {
                if (response.isSuccessful) {
                    val count = response.body()?.size ?: 0
                    tvBookingCount.text = "$count lần đặt bàn"
                }
            }
            override fun onFailure(call: Call<List<Reservation>>, t: Throwable) {}
        })
    }

    private fun showEditProfileDialog() {
        val dialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_edit_profile, null)
        
        val etName = view.findViewById<android.widget.EditText>(R.id.etEditFullName)
        val etPhone = view.findViewById<android.widget.EditText>(R.id.etEditPhone)
        val etEmail = view.findViewById<android.widget.EditText>(R.id.etEditEmail)
        val btnSave = view.findViewById<android.widget.Button>(R.id.btnSaveProfile)

        // Pre-fill data from currentUser
        currentUser?.let { user ->
            etName.setText(user.fullName)
            etPhone.setText(user.phone)
            etEmail.setText(user.email ?: "")
        }

        btnSave.setOnClickListener {
            val updatedUser = User(
                fullName = etName.text.toString(),
                email = etEmail.text.toString(),
                phone = etPhone.text.toString()
            )

            RetrofitClient.userApi.updateProfile(updatedUser).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ProfileActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                        loadProfileData()
                        dialog.dismiss()
                    } else {
                        val errorMsg = try {
                            val errorBody = response.errorBody()?.string()
                            val jObj = org.json.JSONObject(errorBody ?: "")
                            jObj.getString("message")
                        } catch (e: Exception) {
                            "Lỗi cập nhật: ${response.code()}"
                        }
                        Toast.makeText(this@ProfileActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            })
        }
        
        dialog.setContentView(view)
        dialog.show()
    }
}
