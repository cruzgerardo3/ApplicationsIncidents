package com.example.applicationsincidents.ui.login

import android.util.Log
import androidx.lifecycle.*
import com.example.applicationsincidents.data.model.LoginRequest
import com.example.applicationsincidents.data.model.User
import com.example.applicationsincidents.data.api.ApiClient
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    private val apiService = ApiClient.apiService

    private val _loginResult = MutableLiveData<User>()
    val loginResult: LiveData<User> get() = _loginResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _token = MutableLiveData<String>()
    val token: LiveData<String> get() = _token

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = apiService.login(LoginRequest(email, password))
                Log.d("LOGIN", "Código respuesta: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    Log.d("LOGIN", "Login exitoso: ${loginResponse.user.username}")
                    _loginResult.postValue(loginResponse.user)
                    _token.postValue(loginResponse.token)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LOGIN", "Error login: $errorBody")
                    _error.postValue("Credenciales inválidas")
                }
            } catch (e: Exception) {
                Log.e("LOGIN", "Excepción: ${e.message}", e)
                _error.postValue("Error: ${e.message}")
            }
        }
    }
}
