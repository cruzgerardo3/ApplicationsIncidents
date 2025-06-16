package com.example.applicationsincidents.ui.admin.adduser

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationsincidents.R
import com.example.applicationsincidents.data.api.ApiClient
import com.example.applicationsincidents.util.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.RequestBody
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class AddUserActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var spinnerUserType: Spinner
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_user)

        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        spinnerUserType = findViewById(R.id.spinnerUserType)
        btnRegister = findViewById(R.id.btnRegisterUser)

        // Cargar tipos de usuario
        ArrayAdapter.createFromResource(
            this,
            R.array.user_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUserType.adapter = adapter
        }

        btnRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val roleSelected = spinnerUserType.selectedItem.toString().lowercase()
        val idTypeUser = if (roleSelected == "admin") 1 else 2

        // Validación básica
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        // Construir cuerpo JSON
        val json = JSONObject().apply {
            put("username", username)
            put("email", email)
            put("password", password)
            put("id_type_user", idTypeUser)
        }

        val requestBody: RequestBody = json.toString()
            .toRequestBody("application/json".toMediaType())

        val token = TokenManager.getToken(this)
        if (token == null) {
            Toast.makeText(this, "$token Token no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val call = ApiClient.apiService.addUser("Bearer $token", requestBody)
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(
                call: Call<Map<String, Any>>,
                response: Response<Map<String, Any>>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.get("success") == true) {
                        Toast.makeText(this@AddUserActivity, "Usuario creado correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddUserActivity, body?.get("error").toString(), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@AddUserActivity, "Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Toast.makeText(this@AddUserActivity, "Error de red: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
