package com.example.udlearning.ui.screens.usuarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udlearning.data.UserRepository
import com.example.udlearning.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuariosViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _usuarios = MutableStateFlow<List<User>>(emptyList())
    val usuarios: StateFlow<List<User>> = _usuarios

    // Create user form state
    private val _createNombre = MutableStateFlow("")
    val createNombre: StateFlow<String> = _createNombre

    private val _createEmail = MutableStateFlow("")
    val createEmail: StateFlow<String> = _createEmail

    private val _createRol = MutableStateFlow("estudiante")
    val createRol: StateFlow<String> = _createRol

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onNombreChange(value: String) { _createNombre.value = value }
    fun onEmailChange(value: String) { _createEmail.value = value }
    fun onRolChange(value: String) { _createRol.value = value }
    fun clearMensaje() { _mensaje.value = null }

    fun loadUsuarios() {
        viewModelScope.launch {
            userRepository.getAllUsers { userList, _ ->
                _usuarios.value = userList
            }
        }
    }

    fun createUser() {
        val nombre = _createNombre.value.trim()
        val email = _createEmail.value.trim()
        val rol = _createRol.value

        // Validations
        if (nombre.isEmpty()) {
            _mensaje.value = "El nombre no puede estar vacío"
            return
        }
        if (email.isEmpty()) {
            _mensaje.value = "El correo no puede estar vacío"
            return
        }
        if (!email.endsWith("@udistrital.edu.co")) {
            _mensaje.value = "Solo se permiten correos institucionales (@udistrital.edu.co)"
            return
        }

        _isLoading.value = true
        val newUser = User(
            nombre = nombre,
            email = email,
            rol = rol
        )
        userRepository.createUser(newUser) { success, error ->
            _isLoading.value = false
            if (success) {
                _mensaje.value = "Usuario creado exitosamente"
                _createNombre.value = ""
                _createEmail.value = ""
                _createRol.value = "estudiante"
                loadUsuarios()
            } else {
                _mensaje.value = "Error al crear usuario: $error"
            }
        }
    }

    fun updateUser(user: User, newNombre: String, newSemestre: String, newRol: String) {
        val updatedUser = user.copy(
            nombre = newNombre.trim(),
            semestre = newSemestre.trim(),
            rol = newRol
        )
        userRepository.updateUser(updatedUser) { success, error ->
            if (success) {
                _mensaje.value = "Usuario actualizado exitosamente"
                loadUsuarios()
            } else {
                _mensaje.value = "Error al actualizar: $error"
            }
        }
    }

    fun updateUserRole(user: User, newRole: String) {
        val updatedUser = user.copy(rol = newRole)
        userRepository.saveUser(updatedUser) { success, _ ->
            if (success) {
                loadUsuarios()
            }
        }
    }

    fun deleteUser(userId: String) {
        userRepository.deleteUser(userId) { success, _ ->
            if (success) {
                _mensaje.value = "Usuario eliminado"
                loadUsuarios()
            }
        }
    }
}
