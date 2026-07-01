package com.example.appdatmon.data.api

import com.example.appdatmon.RestaurantTable
import retrofit2.Call
import retrofit2.http.*

interface TableApi {
    @GET("api/tables")
    fun getAllTables(): Call<List<TableResponse>>

    @PUT("api/tables/{id}/status")
    fun updateStatus(@Path("id") id: Long, @Body status: Map<String, String>): Call<Void>

    @GET("api/tables/qr/{qrCode}")
    fun getTableByQRCode(@Path("qrCode") qrCode: String): Call<TableResponse>
}

data class TableResponse(
    val id: Long,
    val tableNumber: Int,
    val capacity: Int,
    val status: String, // AVAILABLE, BOOKED, OCCUPIED, CLEANING
    val calculatedStatus: String?,
    val waitingAlert: Boolean?,
    val timeUsed: String?,
    val guestCount: Int?,
    val activeReservation: ReservationResponse?
)

data class ReservationResponse(
    val id: Long,
    val guestName: String,
    val guestPhone: String,
    val reservationTime: String,
    val numberOfGuests: Int,
    val status: String
)
