// com.example.applicationsincidents.ui.admin.incidents.AllIncidentsActivity.kt

package com.example.applicationsincidents.ui.admin.incidents

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationsincidents.databinding.ActivityAllIncidentsBinding
import com.google.android.material.tabs.TabLayoutMediator

class AllIncidentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllIncidentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllIncidentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = IncidentsPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Todos"
                1 -> "Activos"
                2 -> "Resueltos"
                else -> ""
            }
        }.attach()
    }
}
