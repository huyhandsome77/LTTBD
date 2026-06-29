package com.example.appdatmon.data.api

import com.example.appdatmon.data.model.User
import retrofit2.Call
import retrofit2.http.*

interface UserApi {
    @GET("api/users")
    fun getAllUsers(@Query("search") search: String? = null): Call<List<User>>

    @GET("api/users/{id}")
    fun getUserById(@Path("id") id: Long): Call<User>

    @POST("api/users")
    fun createUser(@Body user: User): Call<User>

    @PUT("api/users/{id}")
    fun updateUser(@Path("id") id: Long, @Body user: User): Call<User>

    @DELETE("api/users/{id}")
    fun deleteUser(@Path("id") id: Long): Call<Void>
}
