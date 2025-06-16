package com.example.applicationsincidents.ui.incident

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.applicationsincidents.R
import com.example.applicationsincidents.data.model.Incident

class IncidentAdapter(private var incidents: List<Incident>) :
    RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder>() {

    class IncidentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.txtIncidentTitle)
        val txtDescription: TextView = itemView.findViewById(R.id.txtIncidentDescription)
        val txtStatus: TextView = itemView.findViewById(R.id.txtIncidentStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incident, parent, false)
        return IncidentViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncidentViewHolder, position: Int) {
        val incident = incidents[position]
        holder.txtTitle.text = incident.type_incident
        holder.txtDescription.text = incident.description
        holder.txtStatus.text = "Estado: ${incident.status}"
    }

    override fun getItemCount(): Int = incidents.size

    fun updateData(newIncidents: List<Incident>) {
        incidents = newIncidents
        notifyDataSetChanged()
    }
}
