package com.example.udlearning.data.model

import com.google.firebase.Timestamp

data class SessionAccess(
    val accessId: String = "",
    val sessionId: String = "",
    val userId: String = "",
    val userName: String = "",
    val estado: String = "iniciada", // iniciada, completada
    val puntaje: Int = 0,
    val totalPosible: Int = 0,
    val fechaInicio: Timestamp = Timestamp.now(),
    val fechaCompletado: Timestamp? = null,
    val respuestas: List<UserAnswer> = emptyList()
)
