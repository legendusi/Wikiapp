package com.example.wikiapp.viewModel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wikiapp.data.DataStoreHelper
import com.example.wikiapp.data.SearchResult
import com.example.wikiapp.retrofit.WikipediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import javax.inject.Inject

fun cleanHtml(html: String): String {
    return Jsoup.parse(html).text()
}

data class UiState(
    val searchResults: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val history: List<String> = emptyList(),
)

@HiltViewModel
class WikipediaViewModel @Inject constructor(
    private val repository: WikipediaRepository,
    private val savedStateHandle: SavedStateHandle,
    private val dataStoreHelper: DataStoreHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {

        viewModelScope.launch {
            val persistedHistory = dataStoreHelper.historyFlow.first().toList()
            _uiState.value = _uiState.value.copy(history = persistedHistory)
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

    val someValue: String = savedStateHandle["key"] ?: "Default Value"

    fun saveValue(value: String) {
        savedStateHandle["key"] = value
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
