package com.example.wikiapp.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wikiapp.data.SearchResult
import com.example.wikiapp.retrofit.WikipediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import org.jsoup.Jsoup

fun cleanHtml(html: String): String {
    return Jsoup.parse(html).text()
}

data class UiState(
    val searchResults: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class WikipediaViewModel @Inject constructor(
    private val repository: WikipediaRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Example usage of SavedStateHandle
    val someValue: String = savedStateHandle["key"] ?: "Default Value"

    fun saveValue(value: String) {
        savedStateHandle["key"] = value
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun searchWikipedia(query: String) {
        if (query.isBlank()) return

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                (delay(300))
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
