package com.example.appdatmon

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.Order
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderListFragment : Fragment() {

    private lateinit var rvOrders: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var adapter: OrderAdapter
    private var orderList = mutableListOf<Order>()
    private var currentStatus: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvOrders = view.findViewById(R.id.rvOrders)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        
        rvOrders.layoutManager = LinearLayoutManager(context)
        adapter = OrderAdapter(orderList) { order ->
            // Logic khi nhấn vào đơn hàng (xem chi tiết, CRUD)
            showOrderActionDialog(order)
        }
        rvOrders.adapter = adapter

        setupTabs(view)

        swipeRefresh.setOnRefreshListener {
            loadOrders(currentStatus)
        }

        loadOrders(null)
    }

    private fun setupTabs(view: View) {
        val btnAll = view.findViewById<Button>(R.id.btn_tab_all)
        val btnPending = view.findViewById<Button>(R.id.btn_tab_pending)
        val btnConfirmed = view.findViewById<Button>(R.id.btn_tab_confirmed)
        val btnPreparing = view.findViewById<Button>(R.id.btn_tab_preparing)
        val btnReady = view.findViewById<Button>(R.id.btn_tab_ready)
        val btnCompleted = view.findViewById<Button>(R.id.btn_tab_completed)
        val btnCancelled = view.findViewById<Button>(R.id.btn_tab_cancelled)

        val buttons = listOf(btnAll, btnPending, btnConfirmed, btnPreparing, btnReady, btnCompleted, btnCancelled)

        fun updateTabStyles(selected: Button) {
            buttons.forEach { 
                it.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
            selected.setTextColor(ContextCompat.getColor(requireContext(), R.color.admin_primary))
        }

        btnAll.setOnClickListener {
            currentStatus = null
            updateTabStyles(btnAll)
            loadOrders(null)
        }

        btnPending.setOnClickListener {
            currentStatus = "PENDING"
            updateTabStyles(btnPending)
            loadOrders("PENDING")
        }

        btnConfirmed.setOnClickListener {
            currentStatus = "CONFIRMED"
            updateTabStyles(btnConfirmed)
            loadOrders("CONFIRMED")
        }

        btnPreparing.setOnClickListener {
            currentStatus = "PREPARING"
            updateTabStyles(btnPreparing)
            loadOrders("PREPARING")
        }

        btnReady.setOnClickListener {
            currentStatus = "READY"
            updateTabStyles(btnReady)
            loadOrders("READY")
        }

        btnCompleted.setOnClickListener {
            currentStatus = "COMPLETED"
            updateTabStyles(btnCompleted)
            loadOrders("COMPLETED")
        }

        btnCancelled.setOnClickListener {
            currentStatus = "CANCELLED"
            updateTabStyles(btnCancelled)
            loadOrders("CANCELLED")
        }
        
        updateTabStyles(btnAll)
    }

    private fun loadOrders(status: String?) {
        swipeRefresh.isRefreshing = true
        RetrofitClient.orderApi.getAllOrders(status).enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                swipeRefresh.isRefreshing = false
                if (response.isSuccessful && response.body() != null) {
                    orderList.clear()
                    orderList.addAll(response.body()!!)
                    adapter.updateData(orderList)
                } else {
                    Toast.makeText(context, "Lỗi tải đơn hàng", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                swipeRefresh.isRefreshing = false
                Log.e("OrderListFragment", "onFailure: ${t.message}")
                Toast.makeText(context, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showOrderActionDialog(order: Order) {
        // Tạm thời hiển thị thông báo, sau này có thể mở OrderDetailFragment
        Toast.makeText(context, "Đã chọn đơn hàng #${order.id}", Toast.LENGTH_SHORT).show()
        
        val detailFragment = OrderDetailFragment()
        val bundle = Bundle()
        bundle.putLong("order_id_long", order.id)
        detailFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.content_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }
}
