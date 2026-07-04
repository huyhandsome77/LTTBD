package com.example.appdatmon.data.model

import com.google.gson.annotations.SerializedName

data class Review(
    val id: Long,
    @SerializedName("user_id") val userId: Long? = null,
    val phone: String? = null,
    @SerializedName("dish_name") val dishName: String? = null,
    @SerializedName("content") val comment: String? = null,
    val rating: Int,
    @SerializedName("created_at") val createdAt: String? = null,
    val user: ReviewUser? = null
)

data class ReviewUser(
    val fullName: String?,
    val phone: String?
)
