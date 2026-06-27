package com.example.appdatmon

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.example.appdatmon.ui.auth.RegisterActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<android.view.View>(R.id.btnGoToRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        findViewById<android.view.View>(R.id.btnGoToAdmin).setOnClickListener {
            startActivity(Intent(this, com.example.appdatmon.ui.admin.AdminActivity::class.java))
        }
    }
}