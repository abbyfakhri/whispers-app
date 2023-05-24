package com.freakeinstein.whispers.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/message")
    fun getDataFromServer(
    ): Call<ApiResponse>

}