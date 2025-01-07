package com.example.wikiapp.retrofit

import com.example.wikiapp.data.SearchResult
import com.example.wikiapp.data.WikipediaResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WikipediaRepository @Inject constructor(
    private val apiService: WikipediaApiService
) {
    suspend fun search(query: String): List<SearchResult> {
        try {
            val response: Response<WikipediaResponse> = apiService.search(query = query)
            if (response.isSuccessful) {
                return response.body()?.query?.search ?: emptyList()
            } else {
                throw Exception("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch data: ${e.message}", e)
        }
    }
}