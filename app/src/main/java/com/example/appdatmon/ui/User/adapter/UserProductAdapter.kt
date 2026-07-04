package com.example.appdatmon.ui.User.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appdatmon.R
import com.example.appdatmon.data.api.RetrofitClient
import com.example.appdatmon.data.model.Product
import java.text.NumberFormat
import java.util.*

class UserProductAdapter(
    private var productList: List<Product>,
    private val onAddClick: (Product) -> Unit
) : RecyclerView.Adapter<UserProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFood: ImageView = view.findViewById(R.id.imgFood)
        val tvName: TextView = view.findViewById(R.id.tvFoodName)
        val tvDesc: TextView = view.findViewById(R.id.tvFoodDesc)
        val tvPrice: TextView = view.findViewById(R.id.tvFoodPrice)
        val btnAdd: View = view.findViewById(R.id.btnAddFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = productList[position]
        holder.tvName.text = product.name
        holder.tvDesc.text = product.description
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        holder.tvPrice.text = formatter.format(product.price)

        if (!product.image.isNullOrEmpty()) {
            val fullUrl = if (product.image.startsWith("http")) {
                product.image
            } else {
                "${RetrofitClient.BASE_URL}${product.image.removePrefix("/")}"
            }
            Glide.with(holder.itemView.context)
                .load(fullUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgFood)
        } else {
            holder.imgFood.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.btnAdd.setOnClickListener { onAddClick(product) }
    }

    override fun getItemCount() = productList.size

    fun updateData(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }
}
