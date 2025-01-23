package com.example.wikiapp.viewModel


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun WikipediaHomeScreen(navController: NavController, viewModel: WikipediaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            ) {
                Text("Log Out")
            }

            Button(
                onClick = {
                    navController.navigate("history")
                }
            ) {
                Text("Show History")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search Wikipedia") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.searchWikipedia(query) },
            enabled = query.isNotBlank(),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text("Search")
        }

        Button(
            onClick = { navController.navigate("saved_pages") } // ✅ Correct route
        ) {
            Text("View Offline Pages")
        }
        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            uiState.error != null -> Text(
                uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            uiState.searchResults.isEmpty() -> Text(
                "No results found",
                style = MaterialTheme.typography.bodyMedium
            )
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.searchResults) { result ->
                    WikipediaResultItem(result = result, navController = navController)
                }
            }
        }
    }
}
