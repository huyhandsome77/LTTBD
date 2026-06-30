package com.example.appdatmon.data.api

import com.example.appdatmon.data.model.Order
import retrofit2.Call
import retrofit2.http.*

interface OrderApi {
    @GET("api/orders")
    fun getAllOrders(@Query("status") status: String? = null): Call<List<Order>>

    @GET("api/orders/{id}")
    fun getOrderById(@Path("id") id: Long): Call<Order>

    @PUT("api/orders/{id}/status")
    fun updateOrderStatus(@Path("id") id: Long, @Body statusData: Map<String, String>): Call<Order>

    @DELETE("api/orders/{id}")
    fun deleteOrder(@Path("id") id: Long): Call<Void>

    @GET("api/orders/table/{tableId}")
    fun getCurrentOrderByTable(@Path("tableId") tableId: Long): Call<Order>

    @PUT("api/orders/{id}/pay")
    fun payOrder(@Path("id") id: Long): Call<Map<String, String>>
}
