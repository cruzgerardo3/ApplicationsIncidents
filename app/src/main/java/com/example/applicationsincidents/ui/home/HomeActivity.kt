// com.example.applicationsincidents.ui.home.HomeActivity.kt
package com.example.applicationsincidents.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationsincidents.R
import com.example.applicationsincidents.ui.admin.adduser.AddUserActivity
import com.example.applicationsincidents.ui.admin.incidents.AllIncidentsActivity
import com.example.applicationsincidents.ui.admin.report.ReportActivity
import com.example.applicationsincidents.ui.incident.AddIncidentActivity
import com.example.applicationsincidents.ui.incident.UserIncidentsActivity
import com.example.applicationsincidents.ui.login.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var btnAdminReport: Button
    private lateinit var btnUserIncidents: Button
    private lateinit var btnLogout: Button
    private lateinit var btnGenerateReport: Button // NUEVO
    private lateinit var fabAddUser: FloatingActionButton
    private lateinit var fabAddIncident: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        welcomeText = findViewById(R.id.welcomeText)
        btnAdminReport = findViewById(R.id.btnAdminReport)
        btnUserIncidents = findViewById(R.id.btnUserIncidents)
        btnLogout = findViewById(R.id.btnLogout)
        btnGenerateReport = findViewById(R.id.btnGenerateReport) // NUEVO

        fabAddUser = findViewById(R.id.fabAddUser)
        fabAddIncident = findViewById(R.id.fabAddIncident)

        val role = intent.getStringExtra("role") ?: ""
        val username = intent.getStringExtra("username") ?: ""

        welcomeText.text = "Bienvenido $username ($role)"

        // Mostrar botones según el rol
        when (role.lowercase()) {
            "admin" -> {
                btnAdminReport.visibility = Button.VISIBLE
                btnGenerateReport.visibility = Button.VISIBLE // NUEVO

                btnAdminReport.setOnClickListener {
                    val intent = Intent(this, AllIncidentsActivity::class.java)
                    startActivity(intent)
                }

                btnGenerateReport.setOnClickListener {
                    // TODO: Reemplaza esta actividad por la que genere o muestre el reporte
                    val intent = Intent(this, ReportActivity::class.java)
                    startActivity(intent)
                }

                fabAddUser.visibility = FloatingActionButton.VISIBLE
                fabAddUser.setOnClickListener {
                    val intent = Intent(this, AddUserActivity::class.java)
                    startActivity(intent)
                }
            }

            "empleado", "user" -> {
                btnUserIncidents.visibility = Button.VISIBLE
                btnUserIncidents.setOnClickListener {
                    val intent = Intent(this, UserIncidentsActivity::class.java)
                    startActivity(intent)
                }

                fabAddIncident.visibility = FloatingActionButton.VISIBLE
                fabAddIncident.setOnClickListener {
                    val intent = Intent(this, AddIncidentActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        // Botón cerrar sesión
        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}

