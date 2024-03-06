package com.youtubevideos.api

import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    fun getRequest(@Url url: String): Call<JsonElement>

}