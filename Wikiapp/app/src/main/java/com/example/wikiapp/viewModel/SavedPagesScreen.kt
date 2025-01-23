package com.example.wikiapp.viewModel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SavedPagesScreen(navController: NavController, viewModel: WikipediaViewModel) {
    val savedPages by viewModel.savedPages.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(savedPages) { page ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("offline_page/${page.id}") // Pass ID instead of HTML
                    }
                    .padding(16.dp)
            ) {
                Text(text = page.title, style = MaterialTheme.typography.titleMedium)
                Text(text = page.url, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}