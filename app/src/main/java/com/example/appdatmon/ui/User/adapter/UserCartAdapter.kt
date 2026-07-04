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
import com.example.appdatmon.utils.CartItem
import java.text.NumberFormat
import java.util.*

class UserCartAdapter(
    private var cartItems: List<CartItem>,
    private val onDeleteClick: (CartItem) -> Unit,
    private val onQuantityChange: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<UserCartAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgCart: ImageView = view.findViewById(R.id.imgCart)
        val tvName: TextView = view.findViewById(R.id.tvCartName)
        val tvQty: TextView = view.findViewById(R.id.tvCartQty)
        val tvPrice: TextView = view.findViewById(R.id.tvCartPrice)
        val btnDelete: View = view.findViewById(R.id.btnDeleteCart)
        val btnPlus: View = view.findViewById(R.id.btnPlusCart)
        val btnMinus: View = view.findViewById(R.id.btnMinusCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cartItems[position]
        val product = item.product
        
        holder.tvName.text = product.name
        holder.tvQty.text = item.quantity.toString()
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        holder.tvPrice.text = formatter.format(product.price * item.quantity)

        if (!product.image.isNullOrEmpty()) {
            val fullUrl = if (product.image.startsWith("http")) {
                product.image
            } else {
                "${RetrofitClient.BASE_URL}${product.image.removePrefix("/")}"
            }
            Glide.with(holder.itemView.context)
                .load(fullUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgCart)
        } else {
            holder.imgCart.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
        holder.btnPlus.setOnClickListener { onQuantityChange(item, item.quantity + 1) }
        holder.btnMinus.setOnClickListener { 
            if (item.quantity > 1) {
                onQuantityChange(item, item.quantity - 1)
            } else {
                onDeleteClick(item)
            }
        }
    }

    override fun getItemCount() = cartItems.size

    fun updateData(newList: List<CartItem>) {
        cartItems = newList
        notifyDataSetChanged()
    }
}
