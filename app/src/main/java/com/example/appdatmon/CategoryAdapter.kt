package com.example.appdatmon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.appdatmon.data.model.Category
import com.example.appdatmon.ui.admin.ChiTietDanhMucFragment

class CategoryAdapter(
    private val categoryList: List<Category>,
    private val onEdit: (Category) -> Unit,
    private val onDelete: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

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
        
        holder.tvCatCode.text = category.id?.toString() ?: "-"
        holder.tvCatName.text = category.name
        holder.tvCatCount.text = category.productCount.toString()

        holder.itemView.setOnClickListener {
            val activity = holder.itemView.context as? AppCompatActivity
            val fragment = ChiTietDanhMucFragment()
            val bundle = Bundle()
            bundle.putLong("CATEGORY_ID", category.id ?: -1L)
            bundle.putString("CATEGORY_NAME", category.name)
            fragment.arguments = bundle

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.content_container, fragment)
                ?.addToBackStack(null)
                ?.commit()
        }

        holder.ivEdit.setOnClickListener { onEdit(category) }
        holder.ivDelete.setOnClickListener { onDelete(category) }
    }

    override fun getItemCount(): Int = categoryList.size
}
