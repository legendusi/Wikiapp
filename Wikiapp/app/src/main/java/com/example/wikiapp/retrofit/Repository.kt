package com.example.wikiapp.retrofit

import com.example.wikiapp.data.SearchResult
import com.example.wikiapp.data.WikipediaResponse
import retrofit2.Response
import javax.inject.Inject

class WikipediaRepository @Inject constructor(
    private val apiService: WikipediaApiService
) {

    // A function that fetches search results from Wikipedia
    suspend fun search(query: String): List<SearchResult> {
        // Call the API service with the query
        val response: Response<WikipediaResponse> = apiService.search(query = query) // Pass the query here

        // If the response is successful, return the search results
        return if (response.isSuccessful) {
            // Extract the search results from the response body
            response.body()?.query?.search ?: emptyList()
        } else {
            // Handle the failure case (e.g., log the error, return an empty list, etc.)
            emptyList()  // Return an empty list if the request failed
        }
    }
}
