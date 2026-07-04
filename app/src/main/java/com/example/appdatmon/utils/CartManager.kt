package com.example.appdatmon.utils

import com.example.appdatmon.data.model.Product

data class CartItem(
    val product: Product,
    var quantity: Int
)

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun addProduct(product: Product) {
        val existingItem = cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(product, 1))
        }
    }

    fun removeProduct(productId: Long) {
        cartItems.removeAll { it.product.id == productId }
    }

    fun updateQuantity(productId: Long, newQuantity: Int) {
        val item = cartItems.find { it.product.id == productId }
        if (item != null) {
            if (newQuantity > 0) {
                item.quantity = newQuantity
            } else {
                removeProduct(productId)
            }
        }
    }

    fun getItems(): List<CartItem> = cartItems

    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.product.price * it.quantity }
    }

    fun getCount(): Int {
        return cartItems.sumOf { it.quantity }
    }

    fun clear() {
        cartItems.clear()
    }
}
