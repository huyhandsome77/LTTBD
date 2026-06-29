package com.example.appdatmon.data.api

import com.example.appdatmon.data.model.KitchenItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface KitchenApi {
    @GET("api/kitchen/items")
    fun getKitchenItems(): Call<List<KitchenItem>>

    @PUT("api/kitchen/items/{id}/status")
    fun updateStatus(
        @Path("id") id: Long,
        @Body status: Map<String, String>
    ): Call<ResponseBody>

    @POST("api/kitchen/test-item")
    fun createTestItem(
        @Body body: Map<String, String>
    ): Call<ResponseBody>
}
