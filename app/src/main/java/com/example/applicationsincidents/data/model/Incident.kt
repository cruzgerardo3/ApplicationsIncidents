package com.example.applicationsincidents.data.model

data class Incident(
    val id_incident: Int,
    val type_incident: String,
    val description: String,
    val date_register: String,
    val status: String,
    val resolution: String?,
    val image_path: String?,
    val username: String,
    val id_user: Int
)
