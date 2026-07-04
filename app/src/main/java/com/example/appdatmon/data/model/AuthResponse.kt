package com.example.appdatmon.data.model

data class RegisterResponse(
    val message: String,
    val user: User?
)

data class LoginResponse(
    val message: String,
    val token: String?,
    val user: User?
)

data class User(
    val id: Long? = null,
    val fullName: String,
    val username: String? = null,
    val email: String? = null,
    val phone: String,
    val points: Int? = 0,
    val role: String? = null,
    val status: String? = null,
    val password: String? = null
)
