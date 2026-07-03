package com.example.appdatmon.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PointApi {
    @POST("api/points/add-points")
    fun addPoints(@Body request: PointRequest): Call<PointResponse>
}

data class PointRequest(
    val phone: String,
    val orderId: Long
)

data class PointResponse(
    val message: String,
    val earnedPoints: Int,
    val totalPoints: Int
)
