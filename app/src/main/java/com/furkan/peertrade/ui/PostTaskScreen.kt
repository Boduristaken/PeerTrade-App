package com.furkan.peertrade.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.furkan.peertrade.model.Task
import com.furkan.peertrade.model.User
import com.furkan.peertrade.repo.FirestoreRepo

@Composable
fun PostTaskScreen(
    modifier: Modifier = Modifier,
    userId: String,
    onTaskPosted: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var credits by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        FirestoreRepo.getUser(userId) {
            user = it
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Post a New Task", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = credits,
            onValueChange = { credits = it },
            label = { Text("Credits") },
            modifier = Modifier.fillMaxWidth()
        )

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val creditAmount = credits.toIntOrNull()
            if (title.isBlank() || description.isBlank() || creditAmount == null) {
                error = "Please fill all fields correctly."
                return@Button
            }

            val currentCredits = user?.credits ?: 0
            if (creditAmount > currentCredits) {
                error = "Not enough credits. You have $currentCredits."
                return@Button
            }

            val task = Task(
                title = title,
                description = description,
                creditAmount = creditAmount,
                createdBy = userId
            )

            FirestoreRepo.postTask(task,
                onSuccess = {
                    title = ""; description = ""; credits = ""; error = ""
                    onTaskPosted()
                },
                onFailure = { error = "Failed to post: ${it.message}" }
            )
        }) {
            Text("Post Task")
        }
    }
}