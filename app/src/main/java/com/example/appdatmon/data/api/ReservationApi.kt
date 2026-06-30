package com.example.appdatmon.data.api

import com.example.appdatmon.data.model.Reservation
import retrofit2.Call
import retrofit2.http.*

interface ReservationApi {
    @GET("api/reservations")
    fun getAllReservations(): Call<List<Reservation>>

    @PUT("api/reservations/{id}/check-in")
    fun checkIn(@Path("id") id: Long): Call<Map<String, String>>

    @PUT("api/reservations/{id}/cancel")
    fun cancel(@Path("id") id: Long): Call<Map<String, String>>
}
