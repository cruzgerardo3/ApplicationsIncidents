package com.example.applicationsincidents.ui.home

import androidx.lifecycle.*
import com.example.applicationsincidents.data.model.Incident
import com.example.applicationsincidents.repository.IncidentRepository
import kotlinx.coroutines.launch

class IncidentViewModel(private val repository: IncidentRepository) : ViewModel() {

    private val _incidents = MutableLiveData<List<Incident>>()
    val incidents: LiveData<List<Incident>> get() = _incidents

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchIncidents(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.getIncidents(token)
                if (response.isSuccessful && response.body()?.success == true) {
                    _incidents.value = response.body()?.data ?: emptyList()
                } else {
                    _error.value = "Error: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                _error.value = "Exception: ${e.message}"
            }
        }
    }


}
