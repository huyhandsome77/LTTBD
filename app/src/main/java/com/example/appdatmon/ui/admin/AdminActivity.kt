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
import com.example.appdatmon.UserListFragment
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

        // Bottom Navigation Buttons
        val btnProduct = findViewById<View>(R.id.btn_menu_product)
        val btnTable = findViewById<View>(R.id.btn_menu_table)
        val btnUser = findViewById<View>(R.id.btn_menu_user)
        val btnOrder = findViewById<View>(R.id.btn_menu_order)

        // Xử lý nút tắt Menu trong Header
        val headerView = navView.getHeaderView(0)
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
                    tvHomeWelcome.visibility = View.GONE // Ẩn chữ chào mừng trang chủ
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_container, AdminReviewsFragment())
                        .commit()
                }
                R.id.nav_points -> {
                    tvTitle.text = "Quản lý tích điểm"
                    tvHomeWelcome.visibility = View.GONE
                    // supportFragmentManager.beginTransaction().replace(R.id.content, AdminPointsFragment()).commit()
                }
                R.id.nav_stats -> {
                    tvTitle.text = "Xem thống kê"
                    tvHomeWelcome.visibility = View.GONE
                    // supportFragmentManager.beginTransaction().replace(R.id.content, AdminStatsFragment()).commit()
                }
                R.id.nav_kitchen -> {
                    tvTitle.text = "Bộ phận bếp"
                    tvHomeWelcome.visibility = View.GONE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_container, KitchenFragment())
                        .commit()
                }
                R.id.nav_settings -> {
                    tvTitle.text = "Cài đặt"
                    tvHomeWelcome.visibility = View.GONE
                    // Xử lý cài đặt
                }
                R.id.nav_logout -> {
                    // Xử lý đăng xuất
                    com.example.appdatmon.data.api.AuthManager.clear(this)
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
            // Gỡ bỏ fragment hiện tại nếu có để hiện lại nội dung trang chủ
            val fragment = supportFragmentManager.findFragmentById(R.id.content_container)
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }

        // Xử lý Bottom Navigation
        btnProduct?.setOnClickListener {
            tvTitle.text = "Quản lý sản phẩm"
            tvHomeWelcome.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_container, QuanLyDanhMucSanPhamFragment())
                .commit()
        }

        btnTable?.setOnClickListener {
            tvTitle.text = "Quản lý bàn"
            tvHomeWelcome.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_container, QuanLyBanFragment())
                .commit()
        }

        btnUser?.setOnClickListener {
            tvTitle.text = "Quản lý người dùng"
            tvHomeWelcome.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_container, UserListFragment())
                .commit()
        }

        btnOrder?.setOnClickListener {
            tvTitle.text = "Quản lý đơn hàng"
            tvHomeWelcome.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_container, OrderListFragment())
                .commit()
        }
    }
}