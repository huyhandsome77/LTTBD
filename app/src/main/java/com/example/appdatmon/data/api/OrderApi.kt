package com.example.appdatmon.data.api

import com.example.appdatmon.data.model.Order
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface OrderApi {
    @GET("api/orders/table/{tableId}")
    fun getCurrentOrderByTable(@Path("tableId") tableId: Long): Call<Order>

    @PUT("api/orders/{id}/pay")
    fun payOrder(@Path("id") id: Long): Call<Map<String, String>>
}
