package com.furkan.peertrade.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.furkan.peertrade.viewmodel.UserViewModel

@Composable
fun MainScreen(
    currentUserId: String, // ✅ Fix this
    userViewModel: UserViewModel,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = { TopBar(userViewModel, onLogout) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Post") },
                    icon = {}
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Browse") },
                    icon = {}
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    label = { Text("My Tasks") },
                    icon = {}
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> PostTaskScreen(
                modifier = Modifier.padding(paddingValues),
                userId = currentUserId,
                onTaskPosted = { userViewModel.refreshUser() }
            )
            1 -> OpenTasksScreen(
                modifier = Modifier.padding(paddingValues),
                currentUserId = currentUserId,
                onTaskAccepted = { userViewModel.refreshUser() }
            )
            2 -> MyTasksScreen(
                modifier = Modifier.padding(paddingValues),
                userId = currentUserId,
                onTaskStatusChanged = { userViewModel.refreshUser() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(userViewModel: UserViewModel, onLogout: () -> Unit) {
    val userState by userViewModel.user.collectAsState()

    TopAppBar(
        title = { Text("PeerTrade") },
        actions = {
            userState?.let {
                Text("Credits: ${it.credits}", style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = onLogout) {
                Text("Logout")
            }
        }
    )
}