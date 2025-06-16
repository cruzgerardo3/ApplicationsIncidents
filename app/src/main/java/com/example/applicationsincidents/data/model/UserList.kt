package com.example.applicationsincidents.data.model

data class UserList(
    val id_user: Int,
    val username: String,
    val email: String,
    val role: String
) {
    override fun toString(): String = username
}