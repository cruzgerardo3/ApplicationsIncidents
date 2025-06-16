package com.example.applicationsincidents.ui.login

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.applicationsincidents.databinding.ActivityLoginBinding
import android.content.Intent
import android.util.Log
import android.widget.TextView
import com.example.applicationsincidents.ui.home.HomeActivity
import com.example.applicationsincidents.util.TokenManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Campos vacíos", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(email, password)
            }
        }


        viewModel.token.observe(this) { token ->
            TokenManager.saveToken(applicationContext, token)
            Log.d("TOKEN", "Token guardado: $token")
        }

        viewModel.loginResult.observe(this) { user ->
            Toast.makeText(this, "Bienvenido ${user.username}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("username", user.username)
            intent.putExtra("role", user.role)
            startActivity(intent)
            finish()
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }


    }
}
