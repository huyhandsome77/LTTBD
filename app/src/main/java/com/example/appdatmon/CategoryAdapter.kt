package com.example.appdatmon

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.ui.admin.ChiTietDanhMucActivity

class CategoryAdapter(private val categoryList: List<Category>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCatCode: TextView = itemView.findViewById(R.id.tvCatCode)
        val tvCatName: TextView = itemView.findViewById(R.id.tvCatName)
        val tvCatCount: TextView = itemView.findViewById(R.id.tvCatCount)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.tvCatCode.text = category.id
        holder.tvCatName.text = category.name
        holder.tvCatCount.text = category.count.toString()
        //Khi click vào bất kỳ vị trí nào trên dòng danh mục
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            // Tạo lệnh chuyển từ màn hình hiện tại sang màn hình Chi Tiết Danh Mục
            val intent = Intent(context, ChiTietDanhMucActivity::class.java)
            intent.putExtra("CATEGORY_NAME", category.name)
            context.startActivity(intent)
        }

        holder.ivEdit.setOnClickListener { /* Xử lý sửa */ }
        holder.ivDelete.setOnClickListener { /* Xử lý xóa */ }
    }

    override fun getItemCount(): Int = categoryList.size
}