package com.example.wikiapp.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wikiapp.data.DataStoreHelper
import com.example.wikiapp.data.SavedPage
import com.example.wikiapp.data.SearchResult
import com.example.wikiapp.retrofit.WikipediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class WikipediaViewModel @Inject constructor(
    private val repository: WikipediaRepository,
    private val savedStateHandle: SavedStateHandle,
    private val dataStoreHelper: DataStoreHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val _savedPages = MutableStateFlow<List<SavedPage>>(emptyList())
    val savedPages: StateFlow<List<SavedPage>> = _savedPages

    init {
        loadPersistedData()
    }
    fun getPageById(id: Long): Flow<SavedPage?> {
        return repository.getPageById(id)
            .onEach { page ->
                if (page == null) {
                    Log.w("OfflinePage", "Page not found for ID: $id")
                }
            }
    }
    private fun loadPersistedData() {
        viewModelScope.launch {
            val persistedHistory = dataStoreHelper.historyFlow.first().toList()
            _uiState.value = _uiState.value.copy(history = persistedHistory)

            repository.getSavedPages()
                .onEach { pages ->
                    _savedPages.value = pages
                }
                .launchIn(viewModelScope)
        }
    }

    fun addToHistory(pageTitle: String) {
        Log.d("History", "Adding to history: $pageTitle")
        val currentHistory = _uiState.value.history.toSet()
        if (!currentHistory.contains(pageTitle)) {
            val updatedHistory = currentHistory.plus(pageTitle).toList()
            _uiState.value = _uiState.value.copy(history = updatedHistory)

            viewModelScope.launch {
                dataStoreHelper.saveHistory(updatedHistory.toSet())
            }
        }
    }

    fun clearHistory() {
        _uiState.value = _uiState.value.copy(history = emptyList())
        viewModelScope.launch {
            dataStoreHelper.clearHistory()
        }
    }

    fun savePageForOffline(title: String, url: String, htmlContent: String) {
        viewModelScope.launch {
            try {
                if (htmlContent.isBlank()) throw Exception("Page content is empty")

                val unescapedHtml = android.text.Html.fromHtml(htmlContent, android.text.Html.FROM_HTML_MODE_LEGACY).toString()
                val page = SavedPage(title = title, url = url, htmlContent = unescapedHtml)
                repository.savePage(page)

            } catch (e: Exception) {
                Log.e("SavePage", "Error saving page: ${e.stackTraceToString()}")
                _uiState.value = _uiState.value.copy(error = "Failed to save page: ${e.message}")
            }
        }
    }


    object HtmlSanitizer {
        fun sanitize(html: String): String {
            // Remove unnecessary escaping
            return html.replace("\\\"", "\"")
                .replace("\\n", "\n")
                .replace("\\'", "'")
                .replace("\\u003C", "<")
                .replace("\\u003E", ">")
                .replace("\\u0026", "&")
        }
    }
    fun deletePageById(pageId: Long) {
        viewModelScope.launch {
            // Fetch the page by ID to ensure it exists
            val page = repository.getPageById(pageId).first()
            if (page != null) {
                repository.deletePage(page) // Delete using the SavedPage object
            } else {
                Log.w("DeletePage", "Page with ID $pageId not found")
            }
        }
    }



    fun searchWikipedia(query: String) {
        if (query.isBlank()) return

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                delay(300)
                val results = repository.search(query).map { result ->
                    result.copy(snippet = cleanHtml(result.snippet))
                }
                _uiState.value = _uiState.value.copy(
                    searchResults = results,
                    isLoading = false,
                    error = if (results.isEmpty()) "No results found" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error occurred while fetching data"
                )
            }
        }
    }
}

data class UiState(
    val searchResults: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val history: List<String> = emptyList(),
)

fun cleanHtml(html: String): String = Jsoup.parse(html).text()