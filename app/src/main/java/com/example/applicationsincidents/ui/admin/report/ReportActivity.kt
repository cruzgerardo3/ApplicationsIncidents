package com.example.applicationsincidents.ui.admin.report

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationsincidents.R
import com.example.applicationsincidents.data.api.ApiClient
import com.example.applicationsincidents.data.model.UserList
import com.example.applicationsincidents.util.TokenManager
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import java.io.IOException
import java.io.OutputStream

class ReportActivity : AppCompatActivity() {

    private lateinit var spinnerUser: Spinner
    private lateinit var statusSpinner: Spinner
    private lateinit var btnGenerateReport: Button
    private lateinit var progressBar: ProgressBar

    private var userList: List<UserList> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        spinnerUser = findViewById(R.id.spinnerUser)
        statusSpinner = findViewById(R.id.spinnerStatus)
        btnGenerateReport = findViewById(R.id.btnGenerateReport)
        progressBar = findViewById(R.id.progressBar)

        val statuses = arrayOf("Todos", "Activo", "Resuelto")
        statusSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)

        loadUsers()
        btnGenerateReport.setOnClickListener { generateReport() }
    }

    private fun loadUsers() {
        val token = TokenManager.getToken(this)
        if (token == null) {
            Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getUsers("Bearer $token")
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body()?.success == true) {
                        val loadedUsers = response.body()?.data ?: emptyList()

                        // Agregamos opción "Todos los empleados"
                        val modifiedList = listOf(
                            UserList(0, "Todos los empleados", "", "")
                        ) + loadedUsers

                        userList = modifiedList
                        val adapter = ArrayAdapter(this@ReportActivity, android.R.layout.simple_spinner_item, userList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerUser.adapter = adapter
                    } else {
                        Toast.makeText(this@ReportActivity, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ReportActivity, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun generateReport() {
        val token = TokenManager.getToken(this)
        if (token == null) {
            Toast.makeText(this, "Token no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedUser = spinnerUser.selectedItem as? UserList
        val userId = if (selectedUser?.id_user == 0) null else selectedUser?.id_user?.toString()

        val status = when (statusSpinner.selectedItem.toString()) {
            "Todos" -> null
            else -> statusSpinner.selectedItem.toString()
        }

        progressBar.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.generateReport("Bearer $token", userId, status)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful && response.body() != null) {
                        savePdfToDownloads(response.body()!!)
                    } else {
                        Toast.makeText(this@ReportActivity, "Error al generar el reporte", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@ReportActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun savePdfToDownloads(body: ResponseBody) {
        val fileName = "reporte_incidentes_${System.currentTimeMillis()}.pdf"

        val resolver = contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                val outputStream: OutputStream? = resolver.openOutputStream(it)
                outputStream?.use { stream ->
                    stream.write(body.bytes())
                }
                Toast.makeText(this, "Reporte guardado en Descargas", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                Toast.makeText(this, "Error guardando PDF: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } ?: run {
            Toast.makeText(this, "No se pudo crear el archivo PDF", Toast.LENGTH_LONG).show()
        }
    }
}
