package com.example.appdatmon.data.api

import com.example.appdatmon.data.model.LoginRequest
import com.example.appdatmon.data.model.LoginResponse
import com.example.appdatmon.data.model.RegisterRequest
import com.example.appdatmon.data.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
