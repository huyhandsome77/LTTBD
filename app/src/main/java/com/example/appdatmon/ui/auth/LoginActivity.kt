package com.example.appdatmon.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appdatmon.MainActivity
import com.example.appdatmon.R
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.LoginRequest
import com.example.appdatmon.data.model.LoginResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val txtRegister = findViewById<TextView>(R.id.txtRegister)
        val txtSkip = findViewById<TextView>(R.id.txtSkip)

        btnLogin.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (username.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Username hoặc Số điện thoại", Toast.LENGTH_SHORT).show()
                edtUsername.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show()
                edtPassword.requestFocus()
                return@setOnClickListener
            }

            val request = LoginRequest(username, password) // 'username' ở đây là giá trị từ ô nhập, sẽ map vào field 'account' của LoginRequest
            
            RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val role = body?.user?.role?.uppercase() ?: "NONE"
                        Toast.makeText(this@LoginActivity, "VAI TRÒ TÀI KHOẢN: $role", Toast.LENGTH_LONG).show()

                        when (role) {
                            "ADMIN" -> {
                                val intent = Intent(this@LoginActivity, com.example.appdatmon.ui.admin.AdminActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            "STAFF" -> {
                                val intent = Intent(this@LoginActivity, com.example.appdatmon.ui.staff.StaffActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    } else {
                        val errorMsg = try {
                            val errorBody = response.errorBody()?.string()
                            val jObjError = JSONObject(errorBody ?: "")
                            jObjError.getString("message")
                        } catch (e: Exception) {
                            "Sai tài khoản hoặc mật khẩu"
                        }
                        Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        txtRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        txtSkip.setOnClickListener {
            Toast.makeText(this, "Bạn đang sử dụng ứng dụng với tư cách khách", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
