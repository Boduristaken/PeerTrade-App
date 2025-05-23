package com.furkan.peertrade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.furkan.peertrade.ui.LoginScreen
import com.furkan.peertrade.ui.MainScreen
import com.furkan.peertrade.viewmodel.UserViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)  // ✅ Make sure Firebase is initialized

        setContent {
            var userId by remember { mutableStateOf<String?>(null) }

            // Automatically login if user is already authenticated
            LaunchedEffect(Unit) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                userId = currentUser?.uid
            }

            if (userId == null) {
                LoginScreen(
                    onLoginSuccess = { uid ->
                        userId = uid
                    }
                )
            } else {
                val userViewModel = remember { UserViewModel(userId!!) }
                MainScreen(
                    currentUserId = userId!!,
                    userViewModel = userViewModel,
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        userId = null
                    }
                )
            }
        }
    }
}