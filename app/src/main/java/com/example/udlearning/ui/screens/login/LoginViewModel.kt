package com.example.udlearning.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.AuthRepository

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onEmailChange(value: String) {
        email = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    fun login(onSuccess: () -> Unit) {

        // 🔥 Validaciones
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage = "Todos los campos son obligatorios"
            return
        }

        if (!email.endsWith("@udistrital.edu.co")) {
            errorMessage = "Debe usar correo institucional"
            return
        }

        isLoading = true
        errorMessage = null

        repository.login(email, password) { success, error ->
            isLoading = false
            if (success) {
                onSuccess()
            } else {
                errorMessage = error
            }
        }
    }
}