package com.example.appdatmon

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.Category
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuanLyDanhMucSanPhamFragment : Fragment() {

    private lateinit var adapter: CategoryAdapter
    private var categoryList = mutableListOf<Category>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quanlydanhmucsanpham, container, false)

        val rvCategory = view.findViewById<RecyclerView>(R.id.rvCategory)
        rvCategory.layoutManager = LinearLayoutManager(context)

        adapter = CategoryAdapter(categoryList, 
            onEdit = { category -> 
                // Xử lý sửa danh mục (tương tự như thêm nhưng truyền ID)
            },
            onDelete = { category -> 
                confirmDeleteCategory(category)
            }
        )
        rvCategory.adapter = adapter

        val edtSearch = view.findViewById<EditText>(R.id.edtSmallSearch)
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                loadCategories(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val btnAddNewCategory = view.findViewById<View>(R.id.btnAddNewCategory)
        btnAddNewCategory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_container, CategoryFormFragment())
                .addToBackStack(null)
                .commit()
        }

        loadCategories()
        return view
    }

    private fun confirmDeleteCategory(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa danh mục '${category.name}'? Thao tác này có thể ảnh hưởng đến các sản phẩm thuộc danh mục này.")
            .setPositiveButton("Xóa") { _, _ ->
                deleteCategory(category)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteCategory(category: Category) {
        val id = category.id ?: return
        RetrofitClient.categoryApi.deleteCategory(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Đã xóa danh mục", Toast.LENGTH_SHORT).show()
                    loadCategories()
                } else {
                    Toast.makeText(requireContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadCategories(query: String = "") {
        RetrofitClient.categoryApi.getAllCategories(query).enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    categoryList.clear()
                    response.body()?.let { categoryList.addAll(it) }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Lỗi tải danh mục", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                if (!isAdded) return
                Log.e("QuanLyDanhMuc", "onFailure: ${t.message}")
                Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val edtSearch = view?.findViewById<EditText>(R.id.edtSmallSearch)
        loadCategories(edtSearch?.text?.toString() ?: "")
    }
}
