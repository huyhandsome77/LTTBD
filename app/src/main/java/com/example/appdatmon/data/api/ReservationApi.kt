package com.example.appdatmon.data.api

import com.example.appdatmon.data.model.Reservation
import retrofit2.Call
import retrofit2.http.*

interface ReservationApi {
    @POST("api/reservations")
    fun createReservation(@Body request: CreateReservationRequest): Call<CreateReservationResponse>

    @GET("api/reservations/my-reservations")
    fun getMyReservations(): Call<List<Reservation>>

    @GET("api/reservations")
    fun getAllReservations(): Call<List<Reservation>>

    @PUT("api/reservations/{id}/check-in")
    fun checkIn(@Path("id") id: Long): Call<Map<String, String>>

    @PUT("api/reservations/{id}/cancel")
    fun cancel(@Path("id") id: Long): Call<Map<String, String>>
}

data class CreateReservationRequest(
    val guestName: String,
    val guestPhone: String,
    val reservationTime: String,
    val numberOfGuests: Int,
    val note: String? = null,
    val user_id: Long? = null
)

data class CreateReservationResponse(
    val message: String,
    val data: ReservationData
)

data class ReservationData(
    val id: Long,
    val table_id: Long,
    val user_id: Long?,
    val guestName: String,
    val guestPhone: String,
    val reservationTime: String,
    val numberOfGuests: Int,
    val note: String?,
    val status: String,
    val tableNumber: Int?
)
