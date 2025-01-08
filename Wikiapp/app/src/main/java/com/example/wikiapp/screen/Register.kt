package com.example.wikiapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth()
        )
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Button(
            onClick = {
                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else {
                    isLoading = true
                    registerUser(email, password) { success, error ->
                        isLoading = false
                        if (success) {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        } else {
                            errorMessage = error
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Already have an account? Log in.",
            modifier = Modifier
                .clickable { navController.navigate("login") }
                .padding(top = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

fun registerUser(
    email: String,
    password: String,
    onComplete: (Boolean, String?) -> Unit
) {
    FirebaseAuth.getInstance()
        .createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(true, null)
            } else {
                onComplete(false, task.exception?.localizedMessage)
            }
        }
}
