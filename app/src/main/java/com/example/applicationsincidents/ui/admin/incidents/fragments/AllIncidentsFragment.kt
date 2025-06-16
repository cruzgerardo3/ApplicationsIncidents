package com.example.applicationsincidents.ui.admin.incidents.fragments

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import com.example.applicationsincidents.databinding.FragmentAllIncidentsBinding
import com.example.applicationsincidents.ui.admin.incidents.IncidentsPagerAdapter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.applicationsincidents.data.api.ApiClient
import com.example.applicationsincidents.data.model.Incident
import com.example.applicationsincidents.ui.adapter.IncidentsAdapter
import com.example.applicationsincidents.ui.dialogs.ResolvedIncidentDialogFragment
import com.example.applicationsincidents.util.TokenManager
import kotlinx.coroutines.launch

class AllIncidentsFragment : Fragment() {

    private var _binding: FragmentAllIncidentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: IncidentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllIncidentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = IncidentsAdapter(requireContext()) { incident ->
            // Aquí defines qué pasa cuando haces clic en un incidente
            showResolutionDialog(incident)
        }
          // aún lo crearemos
        binding.recyclerIncidents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerIncidents.adapter = adapter

        fetchIncidents()
    }

    private fun showResolutionDialog(incident: Incident) {
        val dialog = ResolvedIncidentDialogFragment(incident) {
            fetchIncidents() // refrescar al cerrar
        }
        dialog.show(childFragmentManager, "ResolveDialog")
    }

    private fun fetchIncidents() {
        val token = TokenManager.getToken(requireContext())
        if (token == null) {
            Toast.makeText(requireContext(), "Token no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getIncidents("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    val incidents = response.body()!!.data
                    adapter.submitList(incidents)
                } else {
                    Toast.makeText(requireContext(), "Error al cargar incidentes", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

