package com.example.applicationsincidents.data.model

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)


