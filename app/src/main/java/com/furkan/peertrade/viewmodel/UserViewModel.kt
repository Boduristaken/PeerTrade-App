package com.furkan.peertrade.viewmodel

import androidx.lifecycle.ViewModel
import com.furkan.peertrade.model.User
import com.furkan.peertrade.repo.FirestoreRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(val currentUserId: String) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        refreshUser()
    }

    fun refreshUser() {
        FirestoreRepo.getUser(currentUserId) { fetchedUser ->
            _user.value = fetchedUser
        }
    }
}