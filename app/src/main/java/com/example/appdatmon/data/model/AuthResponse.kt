package com.example.appdatmon.data.model

data class RegisterResponse(
    val message: String,
    val user: UserData?
)

data class UserData(
    val id: Long,
    val fullName: String,
    val username: String,
    val phone: String
)
