package com.example.wikiapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wikiapp.data.SearchResult
import com.example.wikiapp.retrofit.WikipediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WikipediaViewModel @Inject constructor(
    private val repository: WikipediaRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun searchWikipedia(query: String) {
        if (query.isBlank()) return

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                _searchResults.value = repository.search(query)
                if (_searchResults.value.isEmpty()) {
                    _error.value = "No results found"
                }
            } catch (e: Exception) {
                _error.value = "Error occurred while fetching data"
            } finally {
                _isLoading.value = false
            }
        }
    }
}