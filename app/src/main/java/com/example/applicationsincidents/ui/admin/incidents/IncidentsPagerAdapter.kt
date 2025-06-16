package com.example.applicationsincidents.ui.admin.incidents

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.applicationsincidents.ui.admin.incidents.fragments.AllIncidentsFragment
import com.example.applicationsincidents.ui.admin.incidents.fragments.ActiveIncidentsFragment
import com.example.applicationsincidents.ui.admin.incidents.fragments.ResolvedIncidentsFragment

class IncidentsPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllIncidentsFragment()
            1 -> ActiveIncidentsFragment()
            2 -> ResolvedIncidentsFragment()
            else -> throw IllegalArgumentException("Posición inválida: $position")
        }
    }
}
