package com.example.appdatmon.ui.admin

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.appdatmon.R
import com.example.appdatmon.QuanLyBanFragment
import com.example.appdatmon.QuanLyDanhMucSanPhamFragment

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Kết nối Activity với file layout bọc ngoài (chứa Top Bar và Bottom Bar)
        setContentView(R.layout.activity_quanlyban)

        // Ánh xạ TextView hiển thị tiêu đề trên Top Bar để thay đổi chữ động
        val tvTopBarTitle = findViewById<TextView>(R.id.tvTopBarTitle)

        // Khi vừa mở màn hình Admin, mặc định nạp giao diện Sơ đồ bàn vào trước
        replaceFragment(QuanLyBanFragment())
        tvTopBarTitle?.text = "Quản lý bàn"

        // Ánh xạ 2 nút bấm Quản lý Sản phẩm và Quản lý Bàn ở thanh Bottom Bar
        val btnQuanLySanPham = findViewById<LinearLayout>(R.id.btnBottomSanPham)
        val btnQuanLyBan = findViewById<LinearLayout>(R.id.btnBottomBan)

        // Bắt sự kiện khi người dùng ấn vào nút "QL Sản phẩm"
        btnQuanLySanPham?.setOnClickListener {
            replaceFragment(QuanLyDanhMucSanPhamFragment()) // Tráo ruột thành màn hình Danh mục
            tvTopBarTitle?.text = "Quản lý danh mục"       // Đổi chữ tiêu đề Top Bar
        }

        // Bắt sự kiện khi người dùng ấn vào nút "QL Bàn"
        btnQuanLyBan?.setOnClickListener {
            replaceFragment(QuanLyBanFragment())           // Tráo ruột về lại màn hình Sơ đồ bàn
            tvTopBarTitle?.text = "Quản lý bàn"            // Đổi chữ tiêu đề Top Bar
        }
    }

    // Hàm chuyên trách việc rút Fragment cũ ra và nhét Fragment mới vào vùng trống FrameLayout (id: content)
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content, fragment)
        fragmentTransaction.commit()
    }
}