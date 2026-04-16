package com.example.udlearning.ui.screens.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.AuthRepository
import com.example.udlearning.data.GroupRepository
import com.example.udlearning.data.UserRepository
import com.example.udlearning.data.model.Group
import com.example.udlearning.data.model.GroupMember
import com.example.udlearning.data.model.User
import com.google.firebase.Timestamp

class RegisterViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository()
    private val groupRepository = GroupRepository()

    var name by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set
        
    var selectedRole by mutableStateOf("estudiante") // estudiante o docente
        private set
        
    var selectedGroup by mutableStateOf<Group?>(null)
        private set
        
    var availableGroups by mutableStateOf<List<Group>>(emptyList())
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        fetchGroups()
    }

    private fun fetchGroups() {
        groupRepository.getGroups { groups, error ->
            if (groups.isNotEmpty()) {
                availableGroups = groups
                selectedGroup = groups.firstOrNull()
            }
        }
    }

    fun onNameChange(value: String) {
        name = value
    }

    fun onEmailChange(value: String) {
        email = value
    }

    fun onPasswordChange(value: String) {
        password = value
    }

    fun onConfirmPasswordChange(value: String) {
        confirmPassword = value
    }

    fun onRoleChange(value: String) {
        selectedRole = value
    }

    fun onGroupChange(group: Group) {
        selectedGroup = group
    }

    fun register() {

        // 🔥 Validaciones
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
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

        authRepository.register(email, password) { success, error, uid ->

            if (success && uid != null) {
                // Paso 2: Crear el documento de usuario en Firestore
                val newUser = User(
                    userId = uid,
                    nombre = name,
                    email = email,
                    rol = selectedRole,
                    groupId = if (selectedRole == "estudiante") selectedGroup?.groupId else null,
                    fechaCreacion = Timestamp.now()
                )
                
                userRepository.saveUser(newUser) { userSuccess, userError ->
                    if (userSuccess) {
                        // Paso 3: Asignar al grupo si es estudiante
                        if (selectedRole == "estudiante" && selectedGroup != null) {
                            val newMember = GroupMember(
                                userId = uid,
                                fechaIngreso = Timestamp.now()
                            )
                            groupRepository.addGroupMember(selectedGroup!!.groupId, newMember) { groupSuccess, _ ->
                                isLoading = false
                                successMessage = "Revisa tu correo para verificar la cuenta"
                                errorMessage = null
                            }
                        } else {
                            // Es docente o no seleccionó grupo (probablemente no necesite asignar grupo base a docentes según plan inicial)
                            isLoading = false
                            successMessage = "Revisa tu correo para verificar la cuenta"
                            errorMessage = null
                        }
                    } else {
                        isLoading = false
                        errorMessage = userError ?: "Error guardando usuario"
                        successMessage = null
                    }
                }
            } else {
                isLoading = false
                errorMessage = error
                successMessage = null
            }
        }
    }
}