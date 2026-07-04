package com.example.appdatmon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.data.model.Order
import java.text.NumberFormat
import java.util.Locale

class OrderAdapter(
    private var orderList: List<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    fun updateData(newList: List<Order>) {
        orderList = newList
        notifyDataSetChanged()
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        val tvCustomerName: TextView = itemView.findViewById(R.id.tvCustomerName)
        val tvTableInfo: TextView = itemView.findViewById(R.id.tvTableInfo)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.tvTotalPrice)
        val tvOrderOriginalPrice: TextView = itemView.findViewById(R.id.tvOrderOriginalPrice)
        val tvOrderDiscountPrice: TextView = itemView.findViewById(R.id.tvOrderDiscountPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        val context = holder.itemView.context

        holder.tvOrderId.text = "#DH${order.id}"
        holder.tvCustomerName.text = "Khách hàng: ${order.User?.fullName ?: "Khách vãng lai"}"
        holder.tvTableInfo.text = "Bàn số: ${order.RestaurantTable?.tableNumber ?: "Mang về"}"

        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        holder.tvOrderOriginalPrice.text = formatter.format(order.totalPrice)
        holder.tvOrderDiscountPrice.text = "-${formatter.format(order.discountAmount)}"
        holder.tvTotalPrice.text = formatter.format(order.finalPrice)

        // Status styling
        holder.tvOrderStatus.text = when (order.status) {
            "PENDING" -> "Chờ xác nhận"
            "CONFIRMED" -> "Đã xác nhận"
            "PREPARING" -> "Đang chế biến"
            "READY" -> "Chờ phục vụ"
            "COMPLETED" -> "Hoàn thành"
            "CANCELLED" -> "Đã hủy"
            else -> order.status
        }

        val statusBg = when (order.status) {
            "PENDING" -> R.drawable.bg_status_inactive
            "CONFIRMED" -> R.drawable.bg_status_active
            "PREPARING" -> R.drawable.bg_status_active
            "READY" -> R.drawable.bg_status_active
            "COMPLETED" -> R.drawable.bg_status_active
            else -> R.drawable.bg_status_inactive
        }
        holder.tvOrderStatus.setBackgroundResource(statusBg)

        holder.itemView.setOnClickListener { onOrderClick(order) }
    }

    override fun getItemCount() = orderList.size
}
