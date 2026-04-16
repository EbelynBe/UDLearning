package com.example.udlearning.ui.screens.recovery

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.AuthRepository

class RecoveryViewModel : ViewModel() {

    private val repository = AuthRepository()

    var email by mutableStateOf("")
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

    fun sendRecoveryEmail() {

        // 🔥 Validaciones
        if (email.isEmpty()) {
            errorMessage = "El correo es obligatorio"
            return
        }

        if (!email.endsWith("@udistrital.edu.co")) {
            errorMessage = "Debe usar correo institucional"
            return
        }

        isLoading = true

        repository.sendPasswordReset(email) { success, error ->

            isLoading = false

            if (success) {
                successMessage = "Revisa tu correo para cambiar la contraseña"
                errorMessage = null
            } else {
                errorMessage = error
                successMessage = null
            }
        }
    }
}