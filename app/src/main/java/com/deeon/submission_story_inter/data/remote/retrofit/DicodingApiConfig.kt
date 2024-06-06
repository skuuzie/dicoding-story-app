package com.deeon.submission_story_inter.data.remote.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DicodingApiConfig {
    var BASE_URL = "https://story-api.dicoding.dev/v1/"

    fun getAuthInterceptor(token: String): Interceptor {
        return Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(requestHeaders)
        }
    }

    fun getApiService(token: String?): DicodingApiService {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
        if (!token.isNullOrEmpty()) {
            val authInterceptor = getAuthInterceptor(token)
            client.addInterceptor(authInterceptor)
        }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client.build())
            .build()
        return retrofit.create(DicodingApiService::class.java)
    }
}