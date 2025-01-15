package com.example.wikiapp.buttons

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ShowHistoryButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate("history")
        }
    ) {
        Text("Show History")
    }
}