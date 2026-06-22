package com.example.appdatmon.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R

// Định nghĩa dữ liệu cho 1 món ăn
data class Product(val name: String, val price: String)

class ChiTietDanhMucActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chitietdanhmuc)

        val ivBack = findViewById<ImageView>(R.id.ivBack)
        val tvDetailTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val rvProducts = findViewById<RecyclerView>(R.id.rvProducts)

        // Nút quay lại màn hình trước
        ivBack.setOnClickListener { finish() }

        // Nhận tên danh mục được truyền sang từ màn hình trước (mặc định là "Món ăn")
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "Món ăn"
        tvDetailTitle.text = "Danh mục: $categoryName"

        // Tạo dữ liệu món ăn mẫu để hiển thị
        val dummyProducts = listOf(
            Product("Cơm chiên hải sản", "45.000 đ"),
            Product("Lẩu thái chua cay", "150.000 đ"),
            Product("Gà rán giòn rụm", "35.000 đ"),
            Product("Mì xào bò", "40.000 đ")
        )

        // Cấu hình RecyclerView hiển thị danh sách
        rvProducts.layoutManager = LinearLayoutManager(this)
        rvProducts.adapter = ProductAdapter(dummyProducts)
    }
}

class ProductAdapter(private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.tvProductName.text = product.name
        holder.tvProductPrice.text = product.price
    }

    override fun getItemCount(): Int = productList.size
}