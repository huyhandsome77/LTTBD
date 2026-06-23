package com.example.appdatmon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lệnh này sẽ tự động chuyển ngay từ màn hình chính sang màn hình Admin của bạn
        val intent = Intent(this, ActivityAdmin::class.java)
        startActivity(intent)
        finish()
    }
}