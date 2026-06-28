package com.example.appdatmon.data.api

import com.example.appdatmon.data.model.Category
import retrofit2.Call
import retrofit2.http.*

interface CategoryApi {
    @GET("api/categories")
    fun getAllCategories(@Query("search") search: String? = null): Call<List<Category>>

    @GET("api/categories/{id}")
    fun getCategoryById(@Path("id") id: Long): Call<Category>

    @POST("api/categories")
    fun createCategory(@Body category: Category): Call<Category>

    @PUT("api/categories/{id}")
    fun updateCategory(@Path("id") id: Long, @Body category: Category): Call<Category>

    @DELETE("api/categories/{id}")
    fun deleteCategory(@Path("id") id: Long): Call<Void>
}
