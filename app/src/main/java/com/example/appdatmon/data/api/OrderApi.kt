package com.example.appdatmon.data.api

import com.example.appdatmon.data.model.Order
import retrofit2.Call
import retrofit2.http.*

interface OrderApi {
    @POST("api/orders")
    fun createOrder(@Body orderData: OrderRequest): Call<OrderResponse>

    @GET("api/orders/my-orders")
    fun getMyOrders(): Call<List<Order>>

    @GET("api/orders")
    fun getAllOrders(@Query("status") status: String? = null): Call<List<Order>>

    @GET("api/orders/{id}")
    fun getOrderById(@Path("id") id: Long): Call<Order>

    @PUT("api/orders/{id}/status")
    fun updateOrderStatus(@Path("id") id: Long, @Body statusData: Map<String, String>): Call<Order>

    @DELETE("api/orders/{id}")
    fun deleteOrder(@Path("id") id: Long): Call<Void>

    @GET("api/orders/table/{tableId}")
    fun getCurrentOrderByTable(@Path("tableId") tableId: Long): Call<List<Order>>

    @PUT("api/orders/table/{tableId}/pay-all")
    fun payAllOrdersByTable(@Path("tableId") tableId: Long, @Body paymentData: Map<String, String>): Call<Map<String, String>>

    @GET("api/orders/{id}/payment-qr")
    fun getPaymentQR(@Path("id") id: Long): Call<Map<String, String>>

    @PUT("api/orders/{id}/pay")
    fun payOrder(@Path("id") id: Long, @Body paymentData: Map<String, String>): Call<Map<String, String>>
}

data class OrderRequest(
    val table_id: Long?,
    val user_id: Long?,
    val items: List<OrderItemRequest>,
    val note: String?,
    val used_points: Int? = 0
)

data class OrderItemRequest(
    val product_id: Long,
    val quantity: Int,
    val note: String? = ""
)

data class OrderResponse(
    val message: String,
    val data: Order
)
