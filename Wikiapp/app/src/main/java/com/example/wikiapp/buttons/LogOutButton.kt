package com.example.wikiapp.buttons

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

@Composable
fun LogoutButton(navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    Button(
        onClick = {
            try {
                auth.signOut()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            } catch (e: Exception) {
                Log.e("LogoutError", "Error during logout: ${e.message}", e)
            }
        }
    ) {
        Text(text = "Log Out")
    }
}
