package com.example.wikiapp.retrofit

import com.example.wikiapp.data.SavedPage
import com.example.wikiapp.data.SavedPageDao
import com.example.wikiapp.data.SearchResult
import com.example.wikiapp.data.WikipediaResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class WikipediaRepository @Inject constructor(
    private val apiService: WikipediaApiService,
    private val okHttpClient: OkHttpClient,
    private val savedPageDao: SavedPageDao
) {

    // Database operations
    suspend fun savePage(page: SavedPage) = withContext(Dispatchers.IO) {
        try {
            savedPageDao.insert(page)
        } catch (e: Exception) {
            throw Exception("Failed to save page: ${e.message}")
        }
    }

    suspend fun deletePage(page: SavedPage) = withContext(Dispatchers.IO) {
        try {
            savedPageDao.delete(page)
        } catch (e: Exception) {
            throw Exception("Failed to delete page: ${e.message}")
        }
    }

    fun getSavedPages(): Flow<List<SavedPage>> = savedPageDao.getAllSavedPages()

    // Network operations
    suspend fun search(query: String): List<SearchResult> {
        return withContext(Dispatchers.IO) {
            try {
                val response: Response<WikipediaResponse> = apiService.search(query = query)
                when {
                    response.isSuccessful -> response.body()?.query?.search ?: emptyList()
                    else -> throw Exception("API Error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                throw Exception("Search failed: ${e.message}")
            }
        }
    }

    fun fetchPageContent(title: String): String {
        return try {
            val url = "https://en.wikipedia.org/w/api.php?action=parse&page=$title&format=json&prop=text"
            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            val json = response.body?.string() ?: throw Exception("Empty response")
            parseHtmlContentFromJson(json) ?: throw Exception("Invalid content")
        } catch (e: Exception) {
            throw Exception("Content fetch failed: ${e.message}")
        }
    }

    fun getPageById(id: Long): Flow<SavedPage?> {
        return savedPageDao.getPageById(id)
    }

    private fun parseHtmlContentFromJson(json: String): String? {
        return try {
            val jsonObject = JSONObject(json)
            jsonObject.getJSONObject("parse")
                .getJSONObject("text")
                .getString("*") // Raw HTML from Wikipedia
        } catch (e: Exception) {
            null
        }
    }
}