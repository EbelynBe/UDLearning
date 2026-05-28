package com.example.udlearning.data.model

data class HistorialEstudiante(
    val id: String = "",
    val estudianteId: String = "",
    val actividadId: String = "",
    val nombreActividad: String = "",
    val asignatura: String = "",
    val tipoRegistro: String = "", // "Actividad", "Evaluacion"
    val fechaRealizacion: Long = 0L,
    val estado: String = "Pendiente", // "Completada", "Pendiente"
    val calificacion: Double = 0.0,
    val retroalimentacion: String = ""
)
