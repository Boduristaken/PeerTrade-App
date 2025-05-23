package com.furkan.peertrade.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.furkan.peertrade.model.User
import com.furkan.peertrade.repo.FirestoreRepo
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }
    val auth = remember { FirebaseAuth.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(if (isRegisterMode) "Register" else "Login", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            errorMessage = ""

            if (email.isBlank() || password.isBlank()) {
                errorMessage = "Please enter both email and password."
                return@Button
            }

            val task = if (isRegisterMode) {
                auth.createUserWithEmailAndPassword(email, password)
            } else {
                auth.signInWithEmailAndPassword(email, password)
            }

            task.addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    FirestoreRepo.getUser(uid) { existingUser ->
                        if (existingUser == null && isRegisterMode) {
                            val newUser = User(id = uid, credits = 5)
                            FirestoreRepo.createUser(newUser) {
                                onLoginSuccess(uid)
                            }
                        } else if (existingUser != null || !isRegisterMode) {
                            onLoginSuccess(uid)
                        } else {
                            errorMessage = "Unexpected error during login."
                        }
                    }
                } else {
                    errorMessage = "Authentication succeeded but UID is null."
                }
            }.addOnFailureListener {
                errorMessage = it.message ?: "Authentication failed."
            }
        }) {
            Text(if (isRegisterMode) "Register" else "Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            isRegisterMode = !isRegisterMode
            errorMessage = ""
        }) {
            Text(if (isRegisterMode) "Switch to Login" else "Switch to Register")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}