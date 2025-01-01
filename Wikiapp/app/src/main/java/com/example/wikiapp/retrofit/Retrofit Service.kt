package com.example.wikiapp.retrofit

import com.example.wikiapp.data.WikipediaResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response

interface WikipediaApiService {
    @GET("w/api.php")
    suspend fun search(
        @Query("action") action: String = "query",
        @Query("format") format: String = "json",
        @Query("list") list: String = "search",
        @Query("srsearch") query: String
    ): Response<WikipediaResponse>  // Return a Response object to handle errors

    companion object {
        private const val BASE_URL = "https://en.wikipedia.org/"

        // Create the Retrofit instance and return the service
        fun create(): WikipediaApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)  // Base URL for the Wikipedia API
                .addConverterFactory(GsonConverterFactory.create())  // Gson for parsing JSON responses
                .build()

            return retrofit.create(WikipediaApiService::class.java)
        }
    }
}
