package com.furkan.peertrade.ui

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
fun MyTasksScreen(
    modifier: Modifier = Modifier,
    userId: String,
    onTaskStatusChanged: () -> Unit
) {
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }

    LaunchedEffect(userId) {
        FirestoreRepo.getUserTasks(userId) {
            tasks = it
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("My Tasks", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tasks) { task ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(task.title, style = MaterialTheme.typography.titleMedium)
                        Text(task.description, style = MaterialTheme.typography.bodyMedium)
                        Text("Credits: ${task.creditAmount}", style = MaterialTheme.typography.bodySmall)
                        Text("Status: ${task.status}", style = MaterialTheme.typography.bodySmall)

                        Spacer(modifier = Modifier.height(8.dp))

                        if (task.status == "IN_PROGRESS") {
                            if (task.createdBy == userId) {
                                Button(onClick = {
                                    FirestoreRepo.markTaskFinished(task.id, byPoster = true, onComplete = onTaskStatusChanged)
                                }) {
                                    Text("Confirm Completion")
                                }
                            } else if (task.acceptedBy == userId) {
                                Button(onClick = {
                                    FirestoreRepo.markTaskFinished(task.id, byPoster = false, onComplete = onTaskStatusChanged)
                                }) {
                                    Text("Mark as Done")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}