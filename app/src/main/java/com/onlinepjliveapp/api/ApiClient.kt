package com.onlinepjliveapp.api

import com.google.gson.Gson
import com.onlinepjliveapp.util.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private fun okHttpClient(): OkHttpClient {

        val httpClient = OkHttpClient.Builder().apply {
            connectTimeout(1000, TimeUnit.SECONDS)
            readTimeout(1000, TimeUnit.SECONDS)
            writeTimeout(1000, TimeUnit.SECONDS)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        httpClient.addInterceptor(loggingInterceptor)

        httpClient.addInterceptor { chain ->

            val request = chain.request().newBuilder().apply {
                addHeader("Accept", "application/json")
                addHeader("Content-Type", "application/json")
            }.build()

            chain.proceed(request)
        }

        return httpClient.build()
    }

    fun getClient(): ApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient())
            .addConverterFactory(GsonConverterFactory.create(Gson().newBuilder().setLenient().create()))
            .build()
        return retrofit.create(ApiService::class.java)
    }

}