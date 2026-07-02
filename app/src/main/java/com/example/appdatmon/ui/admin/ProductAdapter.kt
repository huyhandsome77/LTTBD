package com.example.appdatmon.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appdatmon.R
import com.example.appdatmon.data.model.Product
import java.text.NumberFormat
import java.util.*

class ProductAdapter(
    private var productList: List<Product>,
    private val onEdit: (Product) -> Unit,
    private val onDelete: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvProductStatus: TextView = itemView.findViewById(R.id.tvProductStatus)
        val ivEdit: ImageView = itemView.findViewById(R.id.ivEditProduct)
        val ivDelete: ImageView = itemView.findViewById(R.id.ivDeleteProduct)
        val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.tvProductName.text = product.name
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        holder.tvProductPrice.text = formatter.format(product.price)

        if (product.isAvailable) {
            holder.tvProductStatus.text = "Sẵn sàng"
            holder.tvProductStatus.setBackgroundResource(R.drawable.bg_status_active)
        } else {
            holder.tvProductStatus.text = "Tạm hết"
            holder.tvProductStatus.setBackgroundResource(R.drawable.bg_status_inactive)
        }

        if (!product.image.isNullOrEmpty()) {
            val fullUrl = if (product.image.startsWith("http")) {
                product.image
            } else {
                "${com.example.appdatmon.data.api.RetrofitClient.BASE_URL}${product.image.removePrefix("/")}"
            }
            Glide.with(holder.itemView.context)
                .load(fullUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivProductImage)
        } else {
            holder.ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        holder.ivEdit.setOnClickListener { onEdit(product) }
        holder.ivDelete.setOnClickListener { onDelete(product) }
    }

    override fun getItemCount(): Int = productList.size

    fun updateData(newList: List<Product>) {
        productList = newList
        notifyDataSetChanged()
    }
}
