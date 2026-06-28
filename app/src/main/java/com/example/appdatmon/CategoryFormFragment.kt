package com.example.appdatmon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.Category
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryFormFragment : Fragment() {

    private lateinit var edtCategoryName: EditText
    private lateinit var edtCategoryDescription: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var tvBackTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_category_form, container, false)

        edtCategoryName = view.findViewById(R.id.edtCategoryName)
        edtCategoryDescription = view.findViewById(R.id.edtCategoryDescription)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
        tvBackTitle = view.findViewById(R.id.tvBackTitle)

        btnSave.setOnClickListener {
            saveCategory()
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tvBackTitle.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun saveCategory() {
        val name = edtCategoryName.text.toString().trim()
        val description = edtCategoryDescription.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show()
            return
        }

        val category = Category(name = name, description = description)
        
        Log.d("CategoryForm", "Saving Category: $category")

        RetrofitClient.categoryApi.createCategory(category).enqueue(object : Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                if (response.isSuccessful) {
                    Log.d("CategoryForm", "Success: ${response.body()}")
                    Toast.makeText(requireContext(), "Thêm danh mục thành công", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("CategoryForm", "Error: ${response.code()} - $errorBody")
                    Toast.makeText(requireContext(), "Thêm thất bại: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Category>, t: Throwable) {
                Log.e("CategoryForm", "Failure: ${t.message}")
                Toast.makeText(requireContext(), "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
