package com.example.applicationsincidents.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.applicationsincidents.data.api.ApiClient
import com.example.applicationsincidents.data.model.Incident
import com.example.applicationsincidents.data.model.UpdateIncidentRequest
import com.example.applicationsincidents.databinding.DialogResolveIncidentBinding
import com.example.applicationsincidents.util.TokenManager
import kotlinx.coroutines.launch

class ResolvedIncidentDialogFragment(
    private val incident: Incident,
    private val onResolved: () -> Unit
) : DialogFragment() {

    private var _binding: DialogResolveIncidentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogResolveIncidentBinding.inflate(LayoutInflater.from(context))

        return AlertDialog.Builder(requireContext())
            .setTitle("Resolver incidente")
            .setView(binding.root)
            .setPositiveButton("Resolver", null) // Se sobrescribe en onStart
            .setNegativeButton("Cancelar") { _, _ -> dismiss() }
            .create()
    }

    override fun onStart() {
        super.onStart()

        (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            val resolution = binding.editTextResolution.text.toString().trim()

            if (resolution.isEmpty()) {
                binding.editTextResolution.error = "La resolución no puede estar vacía"
                return@setOnClickListener
            }

            updateIncident(incident.id_incident, resolution)
        }
    }

    private fun updateIncident(incidentId: Int, resolutionText: String) {
        val token = TokenManager.getToken(requireContext()) ?: return

        val request = UpdateIncidentRequest(
            id_incident = incidentId,
            status = "resuelto",
            resolution = resolutionText
        )

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.updateIncident("Bearer $token", request)

                if (response.isSuccessful) {
                    // Lee el cuerpo como texto en vez de intentar parsearlo
                    val rawResponse = response.body()?.string() ?: ""
                    Log.d("DIALOG", "Respuesta cruda: $rawResponse")

                    // Validamos si contiene un mensaje de éxito
                    if (!rawResponse.contains("actualizado", ignoreCase = true)) {
                        Toast.makeText(requireContext(), "Incidente resuelto", Toast.LENGTH_SHORT).show()
                        dismiss()
                        onResolved()
                    } else {
                        Toast.makeText(requireContext(), "Respuesta inesperada", Toast.LENGTH_LONG).show()
                    }

                } else {
                    val errorText = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("DIALOG", "Error del servidor: $errorText")
                    Toast.makeText(requireContext(), "Error: $errorText", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de red: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("DIALOG", "Excepción al resolver incidente", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
