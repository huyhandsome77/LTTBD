package com.example.appdatmon.data.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val username: String,
    val password: String
)
