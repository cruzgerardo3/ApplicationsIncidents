package com.example.applicationsincidents.repository

import com.example.applicationsincidents.data.api.ApiService
import com.example.applicationsincidents.data.model.Incident
import com.example.applicationsincidents.data.model.IncidentResponse
import retrofit2.Response

class IncidentRepository(private val apiService: ApiService) {
    suspend fun getIncidents(token: String): Response<IncidentResponse> {
        return apiService.getIncidents("Bearer $token")
    }
}
