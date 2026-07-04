package com.example.appdatmon.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StatApi {
    @GET("api/stats")
    fun getStats(
        @Query("type") type: String, // day, month, year
        @Query("date") date: String // YYYY-MM-DD
    ): Call<StatResponse>
}

data class StatResponse(
    val type: String,
    val totalOrders: Int,
    val totalRevenue: Double
)
