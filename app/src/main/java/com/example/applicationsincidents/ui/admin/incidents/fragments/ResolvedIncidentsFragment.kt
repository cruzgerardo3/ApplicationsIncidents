package com.example.applicationsincidents.ui.admin.incidents.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.applicationsincidents.data.api.ApiClient
import com.example.applicationsincidents.data.model.Incident
import com.example.applicationsincidents.databinding.FragmentResolvedIncidentsBinding
import com.example.applicationsincidents.ui.adapter.IncidentsAdapter
import com.example.applicationsincidents.util.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResolvedIncidentsFragment : Fragment() {

    private var _binding: FragmentResolvedIncidentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: IncidentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResolvedIncidentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = IncidentsAdapter(requireContext()) { incident ->
            showIncidentDetail(incident)
        }

        binding.recyclerIncidents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ResolvedIncidentsFragment.adapter
        }

        fetchResolvedIncidents()
    }

    private fun showIncidentDetail(incident: Incident) {
        Toast.makeText(
            requireContext(),
            "Resuelto: ${incident.type_incident} - ${incident.description}",
            Toast.LENGTH_SHORT
        ).show()

        // Puedes reemplazar este Toast con un diálogo si lo deseas.
        // Por ejemplo: IncidentDetailDialogFragment(incident).show(...)
    }

    private fun fetchResolvedIncidents() {
        val token = TokenManager.getToken(requireContext()) ?: return

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiClient.apiService.getIncidents(
                        token = "Bearer $token",
                        status = "resuelto"
                    )
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val incidents = response.body()?.data ?: emptyList()
                    adapter.submitList(incidents)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(requireContext(), "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("RESOLVED_FRAGMENT", "Error al obtener incidentes: ${e.message}", e)
                Toast.makeText(
                    requireContext(),
                    "Error de red: ${e.localizedMessage ?: "desconocido"}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
