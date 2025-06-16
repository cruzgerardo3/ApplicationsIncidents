package com.example.applicationsincidents.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationsincidents.data.model.Incident
import com.example.applicationsincidents.databinding.ItemIncidentBinding

class IncidentsAdapter(
    private val context: Context,
    private val onItemClick: (Incident) -> Unit
) : RecyclerView.Adapter<IncidentsAdapter.IncidentViewHolder>() {

    private val incidents = mutableListOf<Incident>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentViewHolder {
        val binding = ItemIncidentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IncidentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncidentViewHolder, position: Int) {
        holder.bind(incidents[position])
    }

    override fun getItemCount(): Int = incidents.size

    fun submitList(list: List<Incident>) {
        incidents.clear()
        incidents.addAll(list)
        notifyDataSetChanged()
    }

    inner class IncidentViewHolder(private val binding: ItemIncidentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(incident: Incident) {
            binding.txtIncidentTitle.text = incident.type_incident
            binding.txtIncidentDescription.text = incident.description
            binding.txtIncidentStatus.text = incident.status

            binding.root.setOnClickListener {
                onItemClick(incident)
            }
        }
    }
}

