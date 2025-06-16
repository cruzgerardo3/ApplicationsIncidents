package com.example.applicationsincidents.data.model

data class User(
    val id_user: Int,
    val username: String,
    val email: String,
    val id_type_user: Int,
    val role: String
)