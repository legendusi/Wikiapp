package com.example.wikiapp.viewModel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.items
@Composable
fun HistoryScreen(navController: NavController, viewModel: WikipediaViewModel) {
    // Collect uiState to access the history
    val uiState = viewModel.uiState.collectAsState().value  // Collecting uiState correctly

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "Page History",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(uiState.history) { page ->  // Correct usage of uiState.history here
                Text(
                    text = page,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .clickable {
                            navController.navigate("detail/$page")
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = { viewModel.clearHistory() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear History")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
        }

    }
}
