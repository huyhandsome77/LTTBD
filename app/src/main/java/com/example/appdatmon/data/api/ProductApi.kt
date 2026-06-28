package com.example.appdatmon.data.api

import com.example.appdatmon.data.model.Product
import retrofit2.Call
import retrofit2.http.*

interface ProductApi {
    @GET("api/products")
    fun getAllProducts(
        @Query("category_id") categoryId: Long? = null,
        @Query("search") search: String? = null
    ): Call<List<Product>>

    @GET("api/products/{id}")
    fun getProductById(@Path("id") id: Long): Call<Product>

    @POST("api/products")
    fun createProduct(@Body product: Product): Call<Product>

    @PUT("api/products/{id}")
    fun updateProduct(@Path("id") id: Long, @Body product: Product): Call<Product>

    @DELETE("api/products/{id}")
    fun deleteProduct(@Path("id") id: Long): Call<Void>
}
