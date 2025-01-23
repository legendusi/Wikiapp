package com.example.wikiapp.retrofit

import com.example.wikiapp.data.WikipediaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaApiService {
    @GET("w/api.php")
    suspend fun search(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("list") list: String = "search",
        @Query("srsearch") query: String
    ): Response<WikipediaResponse>

}