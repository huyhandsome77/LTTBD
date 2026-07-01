package com.example.appdatmon.model

// Model cho trạng thái bàn
data class TableOrder(
    val tableId: String,
    val tableName: String,
    var status: String, // "Đang gọi món", "Chờ thanh toán", "Đã thanh toán"...
    val orderItems: List<BillItem> = emptyList()
)
// Model cho từng món ăn trong hóa đơn của bàn đó
data class BillItem(
    val itemName: String,
    val quantity: Int,
    val price: Double
)