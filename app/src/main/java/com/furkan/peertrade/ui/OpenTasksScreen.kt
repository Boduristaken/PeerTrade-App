package com.furkan.peertrade.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.furkan.peertrade.model.Task
import com.furkan.peertrade.repo.FirestoreRepo

@Composable
fun OpenTasksScreen(
    modifier: Modifier = Modifier,
    currentUserId: String,
    onTaskAccepted: () -> Unit
) {
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }

    LaunchedEffect(Unit) {
        FirestoreRepo.getOpenTasks {
            tasks = it.filter { task -> task.createdBy != currentUserId }
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Available Tasks", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            FirestoreRepo.acceptTask(
                                taskId = task.id,
                                userId = currentUserId,
                                onSuccess = { onTaskAccepted() },
                                onFailure = { println("❌ Failed to accept: ${it.message}") }
                            )
                        }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(task.title, style = MaterialTheme.typography.titleMedium)
                        Text(task.description, style = MaterialTheme.typography.bodyMedium)
                        Text("Credits: ${task.creditAmount}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}