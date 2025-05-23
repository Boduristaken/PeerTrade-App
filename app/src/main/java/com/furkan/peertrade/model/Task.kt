package com.furkan.peertrade.model

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val creditAmount: Int = 0,
    val createdBy: String = "",
    val acceptedBy: String? = null,
    val status: String = "OPEN", // IN_PROGRESS, CONFIRMED
    val finishedByPoster: Boolean = false,
    val finishedByAccepter: Boolean = false,
    val involvedUsers: List<String> = listOf()
)