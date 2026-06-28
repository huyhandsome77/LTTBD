package com.example.appdatmon.ui.staff

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.appdatmon.R
import com.example.appdatmon.ui.auth.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.example.appdatmon.QuanLyBanFragment
import com.example.appdatmon.ui.admin.AdminPointsFragment

class StaffActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff)

        drawerLayout = findViewById(R.id.drawerLayout)
        tvTitle = findViewById(R.id.tvTitle)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val navView = findViewById<NavigationView>(R.id.navView)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_staff_tables -> {
                    tvTitle.text = "Sơ đồ bàn"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.staff_container, QuanLyBanFragment())
                        .commit()
                }
                R.id.nav_staff_points -> {
                    tvTitle.text = "Tích điểm"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.staff_container, AdminPointsFragment())
                        .commit()
                }
                R.id.nav_logout -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Mặc định mở Sơ đồ bàn
        if (savedInstanceState == null) {
            navView.setCheckedItem(R.id.nav_staff_tables)
            supportFragmentManager.beginTransaction()
                .replace(R.id.staff_container, QuanLyBanFragment())
                .commit()
        }
    }
}