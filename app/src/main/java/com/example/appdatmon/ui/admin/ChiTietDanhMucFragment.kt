package com.example.appdatmon.ui.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChiTietDanhMucFragment : Fragment() {

    private lateinit var adapter: ProductAdapter
    private var categoryId: Long = -1
    private var categoryName: String = ""
    private var productList = mutableListOf<Product>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_chitietdanhmuc, container, false)

        categoryId = arguments?.getLong("CATEGORY_ID", -1) ?: -1
        categoryName = arguments?.getString("CATEGORY_NAME") ?: "Món ăn"

        val ivBack = view.findViewById<ImageView>(R.id.ivBack)
        val tvDetailTitle = view.findViewById<TextView>(R.id.tvDetailTitle)
        val rvProducts = view.findViewById<RecyclerView>(R.id.rvProducts)
        val btnAdd = view.findViewById<Button>(R.id.btnAddNewProduct)

        tvDetailTitle.text = "Danh mục: $categoryName"
        ivBack.setOnClickListener { parentFragmentManager.popBackStack() }

        adapter = ProductAdapter(productList, 
            onEdit = { product -> openProductForm(product) },
            onDelete = { product -> confirmDeleteProduct(product) }
        )

        rvProducts.layoutManager = LinearLayoutManager(requireContext())
        rvProducts.adapter = adapter

        val edtSearch = view.findViewById<EditText>(R.id.edtSearchProduct)
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadProducts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnAdd.setOnClickListener {
            openProductForm(null)
        }

        loadProducts("")
        return view
    }

    override fun onResume() {
        super.onResume()
        val edtSearch = view?.findViewById<EditText>(R.id.edtSearchProduct)
        loadProducts(edtSearch?.text?.toString() ?: "")
    }

    private fun openProductForm(product: Product?) {
        val fragment = ProductFormFragment()
        val bundle = Bundle()
        bundle.putLong("CATEGORY_ID", categoryId)
        product?.id?.let { bundle.putLong("PRODUCT_ID", it) }
        fragment.arguments = bundle
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun loadProducts(query: String = "") {
        if (categoryId == -1L) return

        RetrofitClient.productApi.getAllProducts(categoryId, query).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    productList.clear()
                    response.body()?.let { productList.addAll(it) }
                    adapter.updateData(productList)
                } else {
                    Toast.makeText(requireContext(), "Lỗi tải món ăn", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                if (!isAdded) return
                Log.e("ChiTietDanhMuc", "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmDeleteProduct(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa món '${product.name}'?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteProduct(product)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteProduct(product: Product) {
        product.id?.let { id ->
            RetrofitClient.productApi.deleteProduct(id).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (!isAdded) return
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Xóa thành công", Toast.LENGTH_SHORT).show()
                        loadProducts()
                    } else {
                        Toast.makeText(requireContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    if (!isAdded) return
                    Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
