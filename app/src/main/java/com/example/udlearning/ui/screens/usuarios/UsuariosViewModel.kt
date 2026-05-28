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

    fun loadUsuarios() {
        viewModelScope.launch {
            userRepository.getAllUsers { userList, _ ->
                _usuarios.value = userList
            }
        }
    }

    fun updateUserRole(user: User, newRole: String) {
        val updatedUser = user.copy(rol = newRole)
        userRepository.saveUser(updatedUser) { success, _ ->
            if (success) {
                loadUsuarios() // Reload list to reflect changes
            }
        }
    }

    fun deleteUser(userId: String) {
        userRepository.deleteUser(userId) { success, _ ->
            if (success) {
                loadUsuarios() // Reload list to reflect changes
            }
        }
    }
}
