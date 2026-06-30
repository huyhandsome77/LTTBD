package com.example.appdatmon.data.model

data class Category(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val image: String? = null,
    val productCount: Int = 0 // Thuộc tính ảo để hiển thị số lượng
)
