package com.example.appdatmon.data.model

data class Order(
    val id: Long,
    val totalPrice: Double,
    val discountAmount: Double,
    val finalPrice: Double,
    val note: String?,
    val status: String,
    val paymentStatus: String,
    val OrderItems: List<OrderItem>? = null
)

data class OrderItem(
    val id: Long,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double,
    val note: String?,
    val status: String,
    val Product: Product? = null
)
