package com.example.wikiapp.viewModel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wikiapp.data.SearchResult
import com.example.wikiapp.screen.LoginScreen
import com.example.wikiapp.screen.RegisterScreen
import com.example.wikiapp.screen.SplashScreen

@Composable
fun WikipediaApp() {
    // Initialize the NavController
    val navController = rememberNavController()

    // Set up the NavHost with all the composable routes
    NavHost(navController = navController, startDestination = "splash") {
        // Splash screen route
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // Login screen route
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        // Home screen route
        composable("home") {
            WikipediaHomeScreen(navController = navController)
        }

        // Detail screen route with argument "title"
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
