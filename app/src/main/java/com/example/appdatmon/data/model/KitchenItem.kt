package com.example.appdatmon.data.model

data class KitchenItem(
    val id: Long,
    val quantity: Int,
    val note: String?,
    val status: String,
    val Product: KitchenProduct?,
    val Order: KitchenOrder?
)

data class KitchenProduct(
    val id: Long,
    val name: String,
    val image: String?
)

data class KitchenOrder(
    val id: Long,
    val status: String,
    val RestaurantTable: KitchenTable?
)

data class KitchenTable(
    val id: Long,
    val tableNumber: Int
)
