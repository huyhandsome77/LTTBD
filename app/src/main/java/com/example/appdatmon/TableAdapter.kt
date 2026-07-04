package com.example.appdatmon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import java.util.Locale

data class RestaurantTable(
    val id: Long? = null,
    val number: Int,
    val status: String, // OCCUPIED, BOOKED, AVAILABLE, CLEANING
    val code: String? = null,
    val timeUsed: String? = null,
    val guestCount: Int = 0,
    val hasPendingItems: Boolean = false,
    val needsPayment: Boolean = false,
    val waitingAlert: Boolean = false
)

class TableAdapter(
    private var tableList: List<RestaurantTable>,
    private val onTableClick: (RestaurantTable) -> Unit
) :
    RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    fun updateData(newList: List<RestaurantTable>) {
        tableList = newList
        notifyDataSetChanged()
    }

    class TableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTableNumber: TextView = itemView.findViewById(R.id.tvTableNumber)
        val tvTableCode: TextView = itemView.findViewById(R.id.tvTableCode)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvGuestCount: TextView = itemView.findViewById(R.id.tvGuestCount)
        val badgeNotify: View = itemView.findViewById(R.id.badgeNotify)
        val layoutTableBackground: View = itemView.findViewById(R.id.layoutTableBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val table = tableList[position]
        val context = holder.itemView.context

        holder.itemView.setOnClickListener { onTableClick(table) }

        holder.tvTableNumber.text = table.number.toString()
        holder.tvTableCode.text = table.code ?: String.format(Locale.getDefault(), "B%02d", table.number)
        holder.tvGuestCount.text = table.guestCount.toString()
        holder.tvTime.text = table.timeUsed ?: "0p"

        // Hiệu ứng scale khi chạm
        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start()
                }
                android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                }
            }
            false
        }

        // Đặt màu chữ mặc định là trắng
        holder.tvTableNumber.setTextColor(ContextCompat.getColor(context, R.color.white))
        holder.tvTableCode.setTextColor(ContextCompat.getColor(context, R.color.white))
        holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.white))

        when (table.status) {
            "OCCUPIED" -> {
                holder.layoutTableBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.table_using))
                holder.tvStatus.text = "Đang dùng"
                holder.tvTime.visibility = View.VISIBLE
                holder.tvGuestCount.visibility = View.VISIBLE
            }
            "BOOKED" -> {
                holder.layoutTableBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.table_reserved))
                holder.tvStatus.text = if (table.waitingAlert) "Chờ khách" else "Đã đặt"
                holder.tvTime.visibility = View.GONE
                holder.tvGuestCount.visibility = View.GONE
            }
            "AVAILABLE" -> {
                holder.layoutTableBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.table_empty))
                holder.tvStatus.text = "Trống"
                holder.tvTime.visibility = View.GONE
                holder.tvGuestCount.visibility = View.GONE
                // Với bàn trống thì đổi màu chữ sang đen cho dễ nhìn trên nền xám
                holder.tvTableNumber.setTextColor(ContextCompat.getColor(context, R.color.black))
                holder.tvTableCode.setTextColor(ContextCompat.getColor(context, R.color.black))
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.black))
            }
            "CLEANING" -> {
                holder.layoutTableBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.table_reserved))
                holder.tvStatus.text = "Dọn dẹp"
                holder.tvTime.visibility = View.GONE
                holder.tvGuestCount.visibility = View.GONE
            }
        }

        // Badge thông báo (Món chờ / Thanh toán)
        if (table.hasPendingItems || table.needsPayment || table.waitingAlert) {
            holder.badgeNotify.visibility = View.VISIBLE
        } else {
            holder.badgeNotify.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = tableList.size
}