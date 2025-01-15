package com.example.wikiapp.viewModel

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wikiapp.authentication.LoginScreen
import com.example.wikiapp.authentication.RegisterScreen
import com.example.wikiapp.authentication.SplashScreen

@Composable
fun WikipediaApp(viewModel: WikipediaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("register") {
            RegisterScreen(navController = navController)
        }
        composable("home") {
            WikipediaHomeScreen(navController = navController, viewModel = viewModel)
        }
        composable("history") {
            HistoryScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = "detail/{title}",
            arguments = listOf(navArgument("title") { defaultValue = "Unknown" })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "Unknown"
            viewModel.addToHistory(title)
            WikipediaDetailScreen(title = title)
        }
    }
}
