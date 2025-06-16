// data/model/IncidentResponse.kt
package com.example.applicationsincidents.data.model

data class IncidentResponse(
    val success: Boolean,
    val data: List<Incident>
)
