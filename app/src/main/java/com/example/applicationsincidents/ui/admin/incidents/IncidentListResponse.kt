package com.example.applicationsincidents.ui.admin.incidents

import com.example.applicationsincidents.data.model.Incident

data class IncidentListResponse(
    val success: Boolean,
    val data: List<Incident>
)
