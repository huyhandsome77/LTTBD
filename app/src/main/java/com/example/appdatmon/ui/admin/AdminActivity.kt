package com.example.appdatmon.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.appdatmon.R
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

        // Mở menu khi nhấn nút 3 gạch
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Xử lý khi nhấn vào các mục trong Menu trượt
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_reviews -> {
                    tvTitle.text = "Quản lý đánh giá"
                    tvHomeWelcome.visibility = View.GONE // Ẩn chữ chào mừng trang chủ
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content, AdminReviewsFragment())
                        .commit()
                }
                R.id.nav_points -> {
                    tvTitle.text = "Quản lý tích điểm"
                    tvHomeWelcome.visibility = View.GONE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content, AdminPointsFragment())
                        .commit()
                }
                R.id.nav_stats -> {
                    tvTitle.text = "Xem thống kê"
                    tvHomeWelcome.visibility = View.GONE
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content, AdminStatsFragment())
                        .commit()
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
            val fragment = supportFragmentManager.findFragmentById(R.id.content)
            if (fragment != null) {
                supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        }
    }
}
