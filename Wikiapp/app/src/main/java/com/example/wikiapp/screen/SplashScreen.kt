package com.example.wikiapp.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            // Navigate to the Register/Login flow if no user is signed in
            navController.navigate("register") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            // Navigate to the Search/Home screen if user is signed in
            navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
