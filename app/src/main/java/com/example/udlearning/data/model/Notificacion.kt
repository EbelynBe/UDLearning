package com.example.udlearning.data.model

data class Notificacion(
    val id: String = "",
    val destinatarioId: String = "",
    val tipo: String = "", // "Nueva Sesion", "Evaluacion"
    val mensaje: String = "",
    val fechaGeneracion: Long = 0L,
    val estado: String = "No Leida"
)
