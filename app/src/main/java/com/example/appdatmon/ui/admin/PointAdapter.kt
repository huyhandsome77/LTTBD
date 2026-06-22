package com.example.appdatmon.ui.admin

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.R
import com.example.appdatmon.data.model.PointRecord

class PointAdapter(
    private var list: List<PointRecord>,
    private val onUpdatePoints: (PointRecord, Int) -> Unit,
    private val onViewHistory: (PointRecord) -> Unit
) : RecyclerView.Adapter<PointAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvId)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPoints: TextView = view.findViewById(R.id.tvPoints)
        val tvAvatarChar: TextView = view.findViewById(R.id.tvAvatarChar)
        val btnPlus: ImageButton = view.findViewById(R.id.btnPlus)
        val btnViewHistory: ImageButton = view.findViewById(R.id.btnViewHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_point, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvId.text = "ID: #${item.id}"
        holder.tvName.text = item.customerName
        holder.tvPoints.text = item.points.toString()
        holder.tvAvatarChar.text = item.customerName.take(1).uppercase()
        
        holder.btnViewHistory.setOnClickListener {
            onViewHistory(item)
        }
        
        holder.btnPlus.setOnClickListener {
            showEditPointDialog(holder.itemView, item, true, position)
        }
    }

    fun updateList(newList: List<PointRecord>) {
        list = newList
        notifyDataSetChanged()
    }

    private fun showEditPointDialog(view: View, item: PointRecord, isPlus: Boolean, position: Int) {
        val context = view.context
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_point, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val edtAmount = dialogView.findViewById<EditText>(R.id.edtPointAmount)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)
        
        if (isPlus) {
            tvTitle.text = "Cộng điểm cho ${item.customerName}"
            btnConfirm.setBackgroundColor(Color.parseColor("#2E7D32"))
        } else {
            tvTitle.text = "Xóa điểm của ${item.customerName}"
            btnConfirm.setBackgroundColor(Color.parseColor("#D32F2F"))
        }
        
        btnCancel.setOnClickListener { dialog.dismiss() }
        
        btnConfirm.setOnClickListener {
            val amountStr = edtAmount.text.toString()
            if (amountStr.isNotEmpty()) {
                val amount = amountStr.toInt()
                val finalChange = if (isPlus) amount else -amount
                
                // Gọi callback để update dữ liệu thật trong Fragment
                onUpdatePoints(item, finalChange)
                
                Toast.makeText(context, "Thành công!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Vui lòng nhập số điểm", Toast.LENGTH_SHORT).show()
            }
        }
        
        dialog.show()
    }

    override fun getItemCount() = list.size
}
