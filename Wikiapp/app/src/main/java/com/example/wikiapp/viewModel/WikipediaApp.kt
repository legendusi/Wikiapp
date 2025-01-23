package com.example.wikiapp.viewModel

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wikiapp.auth.LoginScreen
import com.example.wikiapp.auth.RegisterScreen
import com.example.wikiapp.auth.SplashScreen

@Composable
fun WikipediaApp(viewModel: WikipediaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("home") { WikipediaHomeScreen(navController, viewModel) }
        composable("history") { HistoryScreen(navController, viewModel) }
        composable("saved_pages") { SavedPagesScreen(navController, viewModel) }
        composable(
            route = "detail/{title}",
            arguments = listOf(navArgument("title") { defaultValue = "Unknown" })
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: "Unknown"
            viewModel.addToHistory(title)
            WikipediaDetailScreen(title = title, viewModel = viewModel)
        }
        composable(
            route = "offline_page/{pageId}",
            arguments = listOf(navArgument("pageId") { type = NavType.LongType })
        ) { backStackEntry ->
            val pageId = backStackEntry.arguments?.getLong("pageId") ?: -1L
            OfflinePageScreen(
                pageId = pageId,
                viewModel = viewModel
            ) {
                navController.popBackStack() // Navigate back to the previous screen after deletion
            }
        }
    }
}
