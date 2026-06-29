package com.example.appdatmon.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.KitchenItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KitchenFragment : Fragment() {

    private lateinit var edtTable: EditText
    private lateinit var edtFoodName: EditText
    private lateinit var edtQuantity: EditText
    private lateinit var btnSubmitTestItem: Button
    private lateinit var rvKitchenItems: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var adapter: KitchenFragmentAdapter
    private var kitchenItemList = ArrayList<KitchenItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kitchen, container, false)

        edtTable = view.findViewById(R.id.edtTable)
        edtFoodName = view.findViewById(R.id.edtFoodName)
        edtQuantity = view.findViewById(R.id.edtQuantity)
        btnSubmitTestItem = view.findViewById(R.id.btnSubmitTestItem)
        rvKitchenItems = view.findViewById(R.id.rvKitchenItems)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)

        // Setup Recycler View
        rvKitchenItems.layoutManager = LinearLayoutManager(requireContext())
        adapter = KitchenFragmentAdapter(kitchenItemList) { item, nextStatus ->
            updateItemStatus(item, nextStatus)
        }
        rvKitchenItems.adapter = adapter

        // Submit form
        btnSubmitTestItem.setOnClickListener {
            submitTestItem()
        }

        // Load items initially
        fetchKitchenItems()

        return view
    }

    private fun fetchKitchenItems() {
        RetrofitClient.kitchenApi.getKitchenItems().enqueue(object : Callback<List<KitchenItem>> {
            override fun onResponse(call: Call<List<KitchenItem>>, response: Response<List<KitchenItem>>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    val list = response.body()
                    kitchenItemList.clear()
                    if (list != null) {
                        kitchenItemList.addAll(list)
                    }
                    adapter.updateData(kitchenItemList)

                    if (kitchenItemList.isEmpty()) {
                        tvEmptyState.visibility = View.VISIBLE
                        rvKitchenItems.visibility = View.GONE
                    } else {
                        tvEmptyState.visibility = View.GONE
                        rvKitchenItems.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(requireContext(), "Lấy dữ liệu thất bại: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<KitchenItem>>, t: Throwable) {
                if (!isAdded) return
                Toast.makeText(requireContext(), "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun submitTestItem() {
        val tableName = edtTable.text.toString().trim()
        val foodName = edtFoodName.text.toString().trim()
        val quantityStr = edtQuantity.text.toString().trim()

        if (tableName.isEmpty() || foodName.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val body = mapOf(
            "tableName" to tableName,
            "productName" to foodName,
            "quantity" to quantityStr
        )

        RetrofitClient.kitchenApi.createTestItem(body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Thêm món vào bếp thành công!", Toast.LENGTH_SHORT).show()
                    edtTable.text.clear()
                    edtFoodName.text.clear()
                    edtQuantity.text.clear()
                    fetchKitchenItems()
                } else {
                    Toast.makeText(requireContext(), "Thêm thất bại: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if (!isAdded) return
                Toast.makeText(requireContext(), "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateItemStatus(item: KitchenItem, nextStatus: String) {
        val body = mapOf("status" to nextStatus)
        RetrofitClient.kitchenApi.updateStatus(item.id, body).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    val statusText = if (nextStatus == "COOKING") "bắt đầu chế biến" else "hoàn thành"
                    Toast.makeText(
                        requireContext(),
                        "Món ${item.Product?.name ?: ""} đã $statusText",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchKitchenItems()
                } else {
                    Toast.makeText(requireContext(), "Cập nhật thất bại: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                if (!isAdded) return
                Toast.makeText(requireContext(), "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
