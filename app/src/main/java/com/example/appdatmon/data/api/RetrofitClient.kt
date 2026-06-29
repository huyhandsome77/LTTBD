package com.example.appdatmon.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/"

    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
        
        val token = AuthManager.token
        if (token != null) {
            android.util.Log.d("RetrofitClient", "Adding Token: $token")
            requestBuilder.header("Authorization", "Bearer $token")
        } else {
            android.util.Log.d("RetrofitClient", "No Token found in AuthManager")
        }
        
        val request = requestBuilder.build()
        chain.proceed(request)
    }.build()

    val instance: AuthApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(AuthApi::class.java)
    }

    @JvmStatic
    val userApi: UserApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(UserApi::class.java)
    }

    @JvmStatic
    val productApi: ProductApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ProductApi::class.java)
    }

    @JvmStatic
    val categoryApi: CategoryApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(CategoryApi::class.java)
    }

    @JvmStatic
    val uploadApi: UploadApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(UploadApi::class.java)
    }
}
