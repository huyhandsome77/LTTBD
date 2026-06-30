package com.example.appdatmon.data.model

data class Reservation(
    val id: Long,
    val guestName: String,
    val guestPhone: String,
    val reservationTime: String,
    val numberOfGuests: Int,
    val note: String?,
    val status: String,
    val table: ReservationTableInfo? = null
)

data class ReservationTableInfo(
    val id: Long,
    val tableNumber: Int
)
