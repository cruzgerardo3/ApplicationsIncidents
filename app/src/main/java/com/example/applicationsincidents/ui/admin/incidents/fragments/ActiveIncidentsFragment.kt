package com.example.applicationsincidents.ui.admin.incidents.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.applicationsincidents.data.api.ApiClient
import com.example.applicationsincidents.data.api.ApiService
import com.example.applicationsincidents.data.model.Incident
import com.example.applicationsincidents.databinding.FragmentActiveIncidentsBinding
import com.example.applicationsincidents.databinding.FragmentAllIncidentsBinding
import com.example.applicationsincidents.ui.adapter.IncidentsAdapter
import com.example.applicationsincidents.ui.dialogs.ResolvedIncidentDialogFragment
import com.example.applicationsincidents.util.TokenManager
import kotlinx.coroutines.launch

class ActiveIncidentsFragment : Fragment() {

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
            showResolutionDialog(incident)
        }
        binding.recyclerIncidents.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerIncidents.adapter = adapter

        fetchActiveIncidents()
    }

    private fun fetchActiveIncidents() {
        val token = TokenManager.getToken(requireContext()) ?: return
        val client = ApiClient.apiService

        lifecycleScope.launch {
            try {
                val response = client.getIncidents("Bearer $token", "activo")  // 👈 filtro aquí
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!.data
                    adapter.submitList(data)
                }
            } catch (e: Exception) {
                Log.e("ACTIVE_INCIDENTS", "Error: ${e.message}")
            }
        }
    }

    private fun showResolutionDialog(incident: Incident) {
        val dialog = ResolvedIncidentDialogFragment(incident) {
            fetchActiveIncidents() // refrescar al cerrar
        }
        dialog.show(childFragmentManager, "ResolveDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

