package com.example.appdatmon.ui.User

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.api.TableResponse
import com.example.appdatmon.data.model.Category
import com.example.appdatmon.data.model.Product
import com.example.appdatmon.ui.User.adapter.UserCategoryAdapter
import com.example.appdatmon.ui.User.adapter.UserProductAdapter
import com.example.appdatmon.utils.CartManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private var tableQR: String? = null
    private var tableId: Long? = null
    private var tableNumber: Int? = null

    private lateinit var rvCategories: RecyclerView
    private lateinit var rvFoods: RecyclerView
    private lateinit var tvCartBadge: TextView
    private lateinit var btnCart: CardView

    private var categoryList = mutableListOf<Category>()
    private var productList = mutableListOf<Product>()
    private lateinit var categoryAdapter: UserCategoryAdapter
    private lateinit var productAdapter: UserProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initViews()
        setupAdapters()
        setupListeners()

        // NHẬN DỮ LIỆU QR
        tableQR = intent.getStringExtra("QR_CODE")
        
        if (tableQR != null) {
            fetchTableInfo(tableQR!!)
        }

        loadCategories()
        updateCartBadge()
    }

    private fun initViews() {
        rvCategories = findViewById(R.id.rvCategoriesMenu)
        rvFoods = findViewById(R.id.rvFoodsMenu)
        tvCartBadge = findViewById(R.id.tvCartBadge)
        btnCart = findViewById(R.id.btnCart)

        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFoods.layoutManager = LinearLayoutManager(this)
    }

    private fun setupAdapters() {
        categoryAdapter = UserCategoryAdapter(categoryList) { category, _ ->
            loadProducts(category.id)
        }
        rvCategories.adapter = categoryAdapter

        productAdapter = UserProductAdapter(productList) { product ->
            CartManager.addProduct(product)
            updateCartBadge()
            Toast.makeText(this, "Đã thêm ${product.name} vào giỏ hàng", Toast.LENGTH_SHORT).show()
        }
        rvFoods.adapter = productAdapter
    }

    private fun setupListeners() {
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("TABLE_QR", tableQR)
            intent.putExtra("TABLE_ID", tableId ?: -1L)
            intent.putExtra("TABLE_NUMBER", tableNumber ?: -1)
            startActivity(intent)
        }
    }

    private fun loadCategories() {
        RetrofitClient.categoryApi.getAllCategories().enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful && response.body() != null) {
                    categoryList.clear()
                    categoryList.addAll(response.body()!!)
                    categoryAdapter.notifyDataSetChanged()
                    
                    if (categoryList.isNotEmpty()) {
                        loadProducts(categoryList[0].id)
                    }
                }
            }
            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                Toast.makeText(this@MenuActivity, "Lỗi tải danh mục", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadProducts(categoryId: Long?) {
        RetrofitClient.productApi.getAllProducts(categoryId, null).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful && response.body() != null) {
                    productList.clear()
                    productList.addAll(response.body()!!)
                    productAdapter.updateData(productList)
                }
            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@MenuActivity, "Lỗi tải món ăn", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateCartBadge() {
        val count = CartManager.getCount()
        if (count > 0) {
            tvCartBadge.visibility = View.VISIBLE
            tvCartBadge.text = count.toString()
        } else {
            tvCartBadge.visibility = View.GONE
        }
    }

    private fun fetchTableInfo(qrCode: String) {
        RetrofitClient.tableApi.getTableByQRCode(qrCode).enqueue(object : Callback<TableResponse> {
            override fun onResponse(call: Call<TableResponse>, response: Response<TableResponse>) {
                if (response.isSuccessful) {
                    val table = response.body()
                    tableId = table?.id
                    tableNumber = table?.tableNumber
                    val tvTableNumber = findViewById<TextView>(R.id.tvTableNumber)
                    tvTableNumber.visibility = View.VISIBLE
                    tvTableNumber.text = "Bàn số: ${table?.tableNumber ?: "--"}"
                }
            }
            override fun onFailure(call: Call<TableResponse>, t: Throwable) {}
        })
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
    }

}
