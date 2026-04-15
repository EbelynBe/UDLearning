package com.example.udlearning.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.AuthRepository
import com.google.firebase.auth.FirebaseAuth

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

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    if (auth.currentUser?.isEmailVerified == true) {
                        onResult(true, null)
                    } else {
                        onResult(false, "Debes verificar tu correo")
                    }

                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}