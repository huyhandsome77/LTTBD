package com.example.appdatmon.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appdatmon.R
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.RegisterRequest
import com.example.appdatmon.data.model.RegisterResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etRePassword = findViewById<EditText>(R.id.etRePassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val txtLogin = findViewById<TextView>(R.id.txtLogin)

        txtLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val rePassword = etRePassword.text.toString().trim()

            if (fullName.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Họ và tên", Toast.LENGTH_SHORT).show()
                etFullName.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Email", Toast.LENGTH_SHORT).show()
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Số điện thoại", Toast.LENGTH_SHORT).show()
                etPhone.requestFocus()
                return@setOnClickListener
            }
            if (username.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Tên đăng nhập", Toast.LENGTH_SHORT).show()
                etUsername.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Mật khẩu", Toast.LENGTH_SHORT).show()
                etPassword.requestFocus()
                return@setOnClickListener
            }

            if (password != rePassword) {
                Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show()
                etRePassword.requestFocus()
                return@setOnClickListener
            }

            val request = RegisterRequest(fullName, email, phone, username, password)

            RetrofitClient.instance.register(request).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        Toast.makeText(this@RegisterActivity, body?.message ?: "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        val errorMsg = try {
                            val errorBody = response.errorBody()?.string()
                            val jObjError = JSONObject(errorBody ?: "")
                            jObjError.getString("message")
                        } catch (e: Exception) {
                            "Đăng ký thất bại. Vui lòng thử lại"
                        }
                        Toast.makeText(this@RegisterActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
