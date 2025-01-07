package com.example.wikiapp.viewModel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wikiapp.data.SearchResult

@Composable
fun WikipediaApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            WikipediaSearchScreen(navController = navController)
        }
        composable(
            route = "detail/{title}",
            arguments = listOf(navArgument("title") { defaultValue = "Unknown" })
        ) { backStackEntry ->
            WikipediaDetailScreen(
                title = backStackEntry.arguments?.getString("title") ?: "Unknown"
            )
        }
    }
}


@Composable
fun WikipediaSearchScreen(navController: NavController) {
    val backStackEntry = navController.getBackStackEntry("search")
    val viewModel: WikipediaViewModel = hiltViewModel(backStackEntry)

    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
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

        when {
            uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            uiState.error != null -> Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            uiState.searchResults.isEmpty() -> Text("No results found", style = MaterialTheme.typography.bodyMedium)
            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.searchResults) { result ->
                    WikipediaResultItem(result, navController)
                }
            }
        }
    }
}




@Composable
fun WikipediaResultItem(result: SearchResult, navController: NavController) {
    Column(modifier = Modifier
        .padding(8.dp)
        .clickable {
            navController.navigate("detail/${result.title}")

        }) {
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
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }
}
