package com.example.appdatmon.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.appdatmon.OrderListFragment
import com.example.appdatmon.QuanLyBanFragment
import com.example.appdatmon.QuanLyDanhMucSanPhamFragment
import com.example.appdatmon.R
import com.example.appdatmon.ReservationListFragment
import com.example.appdatmon.UserListFragment
import com.example.appdatmon.data.api.AuthManager
import com.google.android.material.navigation.NavigationView

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val navView = findViewById<NavigationView>(R.id.navView)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvHomeWelcome = findViewById<TextView>(R.id.tvHomeWelcome)
        val homeButton = findViewById<View>(R.id.homeButton)

        // Reset Home state (ẩn fragment nếu có để hiện lời chào)
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
        val tvAdminName = headerView.findViewById<TextView>(R.id.tvAdminName)
        tvAdminName?.text = AuthManager.getUserName(this) ?: "Administrator"

        // Xử lý nút tắt Menu trong Header
        val btnCloseMenu = headerView.findViewById<ImageView>(R.id.btnCloseMenu)
        btnCloseMenu.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        // Mở menu khi nhấn nút 3 gạch
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Xử lý khi nhấn vào các mục trong Menu trượt
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_users -> {
                    tvTitle.text = "Quản lý người dùng"
                    tvHomeWelcome.visibility = View.GONE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_container, UserListFragment())
                        .commit()
                }
                R.id.nav_reviews -> {
                    tvTitle.text = "Quản lý đánh giá"
                    tvHomeWelcome.visibility = View.GONE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_container, AdminReviewsFragment())
                        .commit()
                }
                R.id.nav_points -> {
                    tvTitle.text = "Quản lý tích điểm"
                    tvHomeWelcome.visibility = View.GONE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_container, AdminPointsFragment())
                        .commit()
                }
                R.id.nav_reservations -> {
                    tvTitle.text = "Lịch sử đặt bàn"
                    tvHomeWelcome.visibility = View.GONE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_container, ReservationListFragment())
                        .commit()
                }
                R.id.nav_stats -> {
                    tvTitle.text = "Thống kê doanh thu"
                    tvHomeWelcome.visibility = View.GONE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_container, AdminStatsFragment())
                        .commit()
                }
                R.id.nav_settings -> {
                    tvTitle.text = "Cài đặt"
                    tvHomeWelcome.visibility = View.GONE
                }
                R.id.nav_logout -> {
                    AuthManager.clear(this)
                    val intent = android.content.Intent(this, com.example.appdatmon.ui.auth.LoginActivity::class.java)
                    intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Nhấn nút Home (nút tròn đỏ) để quay về trang chủ
        homeButton.setOnClickListener {
            tvTitle.text = "Trang chủ"
            tvHomeWelcome.visibility = View.VISIBLE
            val fragment = supportFragmentManager.findFragmentById(R.id.content_container)
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }

        // Xử lý Bottom Navigation
        findViewById<View>(R.id.btn_menu_product)?.setOnClickListener {
            tvTitle.text = "Quản lý sản phẩm"
            tvHomeWelcome.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_container, QuanLyDanhMucSanPhamFragment())
                .commit()
        }

        findViewById<View>(R.id.btn_menu_table)?.setOnClickListener {
            tvTitle.text = "Quản lý bàn"
            tvHomeWelcome.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_container, QuanLyBanFragment())
                .commit()
        }

        findViewById<View>(R.id.btn_menu_user)?.setOnClickListener {
            tvTitle.text = "Quản lý người dùng"
            tvHomeWelcome.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_container, UserListFragment())
                .commit()
        }

        findViewById<View>(R.id.btn_menu_order)?.setOnClickListener {
            tvTitle.text = "Quản lý đơn hàng"
            tvHomeWelcome.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_container, OrderListFragment())
                .commit()
        }
    }
}
