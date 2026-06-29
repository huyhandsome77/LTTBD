package com.example.appdatmon.ui.staff

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.appdatmon.OrderListFragment
import com.example.appdatmon.QuanLyBanFragment
import com.example.appdatmon.R
import com.example.appdatmon.data.api.AuthManager
import com.example.appdatmon.ui.auth.LoginActivity
import com.google.android.material.navigation.NavigationView

class StaffActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var tvTitle: TextView
    private lateinit var tvHomeWelcome: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff)

        drawerLayout = findViewById(R.id.drawerLayout)
        tvTitle = findViewById(R.id.tvTitle)
        tvHomeWelcome = findViewById(R.id.tvHomeWelcome)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val navView = findViewById<NavigationView>(R.id.navView)
        val homeButton = findViewById<View>(R.id.homeButton)

        // Reset Home state
        if (savedInstanceState == null) {
            tvTitle.text = "Trang chủ"
            tvHomeWelcome.visibility = View.VISIBLE
            val fragment = supportFragmentManager.findFragmentById(R.id.content_container)
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }

        // Setup Drawer Header
        val headerView = navView.getHeaderView(0)
        val tvStaffName = headerView.findViewById<TextView>(R.id.tvStaffName)
        val btnCloseMenu = headerView.findViewById<ImageView>(R.id.btnCloseMenu)
        
        tvStaffName?.text = AuthManager.getUserName(this) ?: "Nhân viên"
        btnCloseMenu?.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Bottom Navigation Buttons
        val btnTable = findViewById<View>(R.id.btn_menu_table)
        val btnOrder = findViewById<View>(R.id.btn_menu_order)
        val btnPoints = findViewById<View>(R.id.btn_menu_points)
        val btnLogoutBottom = findViewById<View>(R.id.btn_menu_logout_bottom)

        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Handle Side Navigation
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_order -> openOrder()
                R.id.nav_table -> openTable()
                R.id.nav_points -> openPoints()
                R.id.nav_profile -> {
                    // Logic mở thông tin cá nhân
                }
                R.id.nav_logout -> performLogout()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Bottom Nav Logic
        btnTable?.setOnClickListener { openTable() }
        btnOrder?.setOnClickListener { openOrder() }
        btnPoints?.setOnClickListener { openPoints() }
        btnLogoutBottom?.setOnClickListener { performLogout() }

        homeButton.setOnClickListener {
            tvTitle.text = "Trang chủ"
            tvHomeWelcome.visibility = View.VISIBLE
            val fragment = supportFragmentManager.findFragmentById(R.id.content_container)
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
    }

    private fun openTable() {
        tvTitle.text = "Quản lý bàn"
        tvHomeWelcome.visibility = View.GONE
        loadFragment(QuanLyBanFragment())
    }

    private fun openOrder() {
        tvTitle.text = "Quản lý đơn hàng"
        tvHomeWelcome.visibility = View.GONE
        loadFragment(OrderListFragment())
    }

    private fun openPoints() {
        tvTitle.text = "Quản lý tích điểm"
        tvHomeWelcome.visibility = View.GONE
        // loadFragment(AdminPointsFragment()) // Placeholder if needed
    }

    private fun performLogout() {
        AuthManager.clear(this)
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .commit()
    }
}
