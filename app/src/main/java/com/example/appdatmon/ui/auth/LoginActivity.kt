package com.example.appdatmon.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appdatmon.MainActivity
import com.example.appdatmon.R
import com.example.appdatmon.data.api.AuthManager
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

        // Tự động đăng nhập nếu đã có token
        val savedToken = AuthManager.getToken(this)
        val savedRole = AuthManager.getRole(this)
        if (savedToken != null) {
            Toast.makeText(this, "Đã tự động đăng nhập", Toast.LENGTH_SHORT).show()
            redirectToRoleBasedActivity(savedRole)
            return
        }

        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val cbRememberMe = findViewById<CheckBox>(R.id.cbRememberMe)
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

            val request = LoginRequest(username, password)
            
            RetrofitClient.instance.login(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val token = body?.token
                        val role = body?.user?.role
                        
                        if (cbRememberMe.isChecked) {
                            AuthManager.saveAuth(this@LoginActivity, token, role)
                        } else {
                            AuthManager.token = token
                            AuthManager.role = role
                        }

                        Toast.makeText(this@LoginActivity, body?.message ?: "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        redirectToRoleBasedActivity(role)
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

    private fun redirectToRoleBasedActivity(role: String?) {
        if ("ADMIN".equals(role, ignoreCase = true)) {
            val intent = Intent(this, com.example.appdatmon.ui.admin.AdminActivity::class.java)
            startActivity(intent)
        } else if ("KITCHEN".equals(role, ignoreCase = true)) {
            val intent = Intent(this, com.example.appdatmon.ui.kitchen.KitchenActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}
