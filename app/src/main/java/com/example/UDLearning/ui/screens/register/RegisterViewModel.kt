package com.example.UDLearning.ui.screens.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.UDLearning.data.AuthRepository

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun onEmailChange(value: String) {
        email = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    fun onConfirmPasswordChange(value: String) {
        confirmPassword = value
    }

    fun register() {

        // 🔥 Validaciones
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorMessage = "Todos los campos son obligatorios"
            return
        }

        if (!email.endsWith("@udistrital.edu.co")) {
            errorMessage = "Debe usar correo institucional"
            return
        }

        if (password.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        if (password != confirmPassword) {
            errorMessage = "Las contraseñas no coinciden"
            return
        }

        isLoading = true

        repository.register(email, password) { success, error ->

            isLoading = false

            if (success) {
                successMessage = "Revisa tu correo para verificar la cuenta"
                errorMessage = null
            } else {
                errorMessage = error
                successMessage = null
            }
        }
    }
}