package com.example.udlearning.data.model

import com.google.firebase.Timestamp

data class User(
    val userId: String = "",
    val nombre: String = "",
    val email: String = "",
    val rol: String = "estudiante", // estudiante, docente, administrador
    val semestre: String = "",
    val groupId: String? = null,
    val fechaCreacion: Timestamp = Timestamp.now()
)
