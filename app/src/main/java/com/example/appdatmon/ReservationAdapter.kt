package com.example.appdatmon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.data.model.Reservation
import java.text.SimpleDateFormat
import java.util.*

class ReservationAdapter(
    private var resList: List<Reservation>,
    private val onCheckIn: (Reservation) -> Unit,
    private val onCancel: (Reservation) -> Unit
) : RecyclerView.Adapter<ReservationAdapter.ResViewHolder>() {

    fun updateData(newList: List<Reservation>) {
        resList = newList
        notifyDataSetChanged()
    }

    class ResViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvGuestName)
        val tvStatus: TextView = itemView.findViewById(R.id.tvReservationStatus)
        val tvPhone: TextView = itemView.findViewById(R.id.tvGuestPhone)
        val tvTime: TextView = itemView.findViewById(R.id.tvResTime)
        val tvTable: TextView = itemView.findViewById(R.id.tvResTable)
        val btnCancel: Button = itemView.findViewById(R.id.btnCancelRes)
        val btnCheckIn: Button = itemView.findViewById(R.id.btnCheckIn)
        val layoutActions: View = itemView.findViewById(R.id.layoutActions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reservation, parent, false)
        return ResViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResViewHolder, position: Int) {
        val res = resList[position]

        holder.tvName.text = res.guestName
        holder.tvPhone.text = "SĐT: ${res.guestPhone}"
        holder.tvTable.text = "Bàn số: ${res.table?.tableNumber ?: "N/A"}"

        // Format Time
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(res.reservationTime)
            holder.tvTime.text = outputFormat.format(date!!)
        } catch (e: Exception) {
            holder.tvTime.text = res.reservationTime
        }

        // Status styling
        holder.tvStatus.text = when (res.status) {
            "PENDING" -> "Chờ duyệt"
            "CONFIRMED" -> "Đã xác nhận"
            "ARRIVED" -> "Khách đã đến"
            "CHECKED_IN" -> "Đang sử dụng"
            "CANCELLED" -> "Đã hủy"
            "EXPIRED" -> "Đã hết hạn"
            else -> res.status
        }

        val statusBg = when (res.status) {
            "CONFIRMED" -> R.drawable.bg_status_active
            "CHECKED_IN" -> R.drawable.bg_status_active
            else -> R.drawable.bg_status_inactive
        }
        holder.tvStatus.setBackgroundResource(statusBg)

        // Show/Hide Actions
        if (res.status == "CONFIRMED") {
            holder.layoutActions.visibility = View.VISIBLE
            holder.btnCheckIn.setOnClickListener { onCheckIn(res) }
            holder.btnCancel.setOnClickListener { onCancel(res) }
        } else {
            holder.layoutActions.visibility = View.GONE
        }
    }

    override fun getItemCount() = resList.size
}
