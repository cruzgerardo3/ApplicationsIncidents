package com.example.applicationsincidents.ui.incident

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationsincidents.R
import com.example.applicationsincidents.data.api.ApiClient
import com.example.applicationsincidents.data.api.ApiService
import com.example.applicationsincidents.repository.IncidentRepository
import com.example.applicationsincidents.ui.home.IncidentViewModel
import com.example.applicationsincidents.util.TokenManager

class UserIncidentsActivity : AppCompatActivity() {

    private lateinit var viewModel: IncidentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: IncidentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_incidents)

        recyclerView = findViewById(R.id.recyclerIncidents)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = IncidentAdapter(emptyList())
        recyclerView.adapter = adapter

        val token = TokenManager.getToken(this) ?: ""

        val apiService = ApiClient.apiService
        val repository = IncidentRepository(apiService)

        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return IncidentViewModel(repository) as T
                }
            }
        )[IncidentViewModel::class.java]

        viewModel.incidents.observe(this) { incidents ->
            adapter.updateData(incidents)
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                Log.e("INCIDENT_ERROR", it)
            }
        }

        viewModel.fetchIncidents(token)
    }
}
