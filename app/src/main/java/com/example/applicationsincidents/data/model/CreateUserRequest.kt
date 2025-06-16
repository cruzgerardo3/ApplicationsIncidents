package com.example.applicationsincidents.data.model

data class CreateUserRequest(
    val username: String,
    val password: String,
    val role: String
)
