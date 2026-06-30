package com.example.appdatmon.data.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val price: Double,
    val image: String? = null,
    @SerializedName("isAvailable")
    val isAvailable: Boolean = true,
    @SerializedName("category_id")
    val categoryId: Long
)
