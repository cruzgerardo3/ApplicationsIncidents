package com.example.applicationsincidents.data.model

data class UpdateIncidentRequest(
    val id_incident: Int,
    val status: String,
    val resolution: String
)
