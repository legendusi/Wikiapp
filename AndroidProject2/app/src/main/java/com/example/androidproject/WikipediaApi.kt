package com.example.androidproject

import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaApi {
    @GET("w/api.php?format=json&action=query&list=search")
    suspend fun searchWikipedia(@Query("srsearch") query: String): WikipediaResponse
}