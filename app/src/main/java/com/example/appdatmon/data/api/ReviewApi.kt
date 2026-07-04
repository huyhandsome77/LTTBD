package com.example.appdatmon.data.api

import com.example.appdatmon.data.model.Review
import retrofit2.Call
import retrofit2.http.*

interface ReviewApi {
    @GET("api/reviews")
    fun getAllReviews(
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("date") date: String? = null
    ): Call<ReviewListResponse>

    @POST("api/reviews")
    fun createReview(@Body review: Review): Call<ReviewCreateResponse>

    @DELETE("api/reviews/{id}")
    fun deleteReview(@Path("id") id: Long): Call<Void>
}

data class ReviewListResponse(
    val totalItems: Int,
    val totalPages: Int,
    val currentPage: Int,
    val reviews: List<Review>
)

data class ReviewCreateResponse(
    val message: String,
    val review: Review
)
