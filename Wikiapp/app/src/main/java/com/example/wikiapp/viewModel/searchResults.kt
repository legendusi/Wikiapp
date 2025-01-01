package com.example.wikiapp.viewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wikiapp.data.SearchResult

@Composable
fun WikipediaApp() {
    // Inject the ViewModel using Hilt
    val viewModel: WikipediaViewModel = hiltViewModel()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.error.collectAsState()

    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        // Input field for search
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Wikipedia") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                viewModel.searchWikipedia(query)
            },
            enabled = query.isNotBlank(),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text("Search")
        }

        // Display loading spinner while fetching data
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        } else {
            // Show error message if any
            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            // Show search results or no results message
            if (searchResults.isEmpty()) {
                Text("No results found", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(searchResults) { result ->
                        WikipediaResultItem(result)
                    }
                }
            }
        }
    }
}

@Composable
fun WikipediaResultItem(result: SearchResult) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = result.title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = result.snippet,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Divider(modifier = Modifier.padding(vertical = 8.dp))
    }
}
