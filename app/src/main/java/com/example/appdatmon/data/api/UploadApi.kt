package com.example.appdatmon.data.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UploadApi {
    @Multipart
    @POST("api/upload/image")
    fun uploadImage(@Part image: MultipartBody.Part): Call<UploadResponse>
}

data class UploadResponse(
    val imageUrl: String
)
