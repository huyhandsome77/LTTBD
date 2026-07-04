package com.example.appdatmon.data.api

import retrofit2.Call
import retrofit2.http.*

interface PayosApi {
    @POST("api/payos/create-payment-link")
    fun createPaymentLink(@Body request: Map<String, Long>): Call<PayosResponse>

    @GET("api/payos/order-status/{orderId}")
    fun checkOrderStatus(@Path("orderId") orderId: Long): Call<OrderStatusResponse>
}

data class PayosResponse(
    val checkoutUrl: String?,
    val qrCode: String?,
    val status: String?,
    val message: String?
)

data class OrderStatusResponse(
    val status: String,
    val message: String?,
    val payosStatus: String?
)
