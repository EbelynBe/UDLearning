package com.example.udlearning.ui.screens.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.SessionRepository
import com.example.udlearning.data.model.Activity

class CreateActivityViewModel : ViewModel() {

    private val sessionRepository = SessionRepository()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    // Tipo de ejercicio seleccionado
    var selectedType by mutableStateOf("quiz")
        private set

    // Campos comunes
    var enunciado by mutableStateOf("")
        private set
    var puntajeMaximo by mutableStateOf("1")
        private set

    var porcentaje by mutableStateOf("")
        private set

    // Campos para "quiz"
    var options = mutableStateListOf("Opción 1", "Opción 2")
        private set
    var correctAnswerIndex by mutableStateOf(0)
        private set

    // Campos para "emparejamiento"
    // Usaremos dos listas emparejadas por índice
    var matchConcepts = mutableStateListOf("Concepto 1", "Concepto 2")
        private set
    var matchAnswers = mutableStateListOf("Respuesta 1", "Respuesta 2")
        private set

    // Campos para "completar" o "traduccion"
    var expectedAnswer by mutableStateOf("")
        private set

    // Sugerencias de mejora (feedback para el estudiante si falla)
    var feedback by mutableStateOf("")
        private set

    var puntajeTotalActual by mutableStateOf(0)
        private set

    fun onTypeSelected(type: String) {
        selectedType = type
        errorMessage = null
    }

    fun onEnunciadoChange(value: String) { enunciado = value }
    fun onPuntajeChange(value: String) { puntajeMaximo = value }
    fun onPorcentajeChange(value: String) { porcentaje = value }

    // Quiz options management
    fun updateOption(index: Int, value: String) {
        if (index in options.indices) {
            options[index] = value
        }
    }
    fun addOption() {
        options.add("Nueva opción")
    }
    fun removeOption(index: Int) {
        if (options.size > 2) {
            options.removeAt(index)
            if (correctAnswerIndex >= options.size) {
                correctAnswerIndex = options.size - 1
            }
        }
    }
    fun selectCorrectAnswer(index: Int) {
        correctAnswerIndex = index
    }

    // Matching options management
    fun updateMatchConcept(index: Int, value: String) {
        if (index in matchConcepts.indices) matchConcepts[index] = value
    }
    fun updateMatchAnswer(index: Int, value: String) {
        if (index in matchAnswers.indices) matchAnswers[index] = value
    }
    fun addMatchPair() {
        matchConcepts.add("Nuevo concepto")
        matchAnswers.add("Nueva respuesta")
    }
    fun removeMatchPair(index: Int) {
        if (matchConcepts.size > 2) {
            matchConcepts.removeAt(index)
            matchAnswers.removeAt(index)
        }
    }

    // Expected answer management
    fun onExpectedAnswerChange(value: String) { expectedAnswer = value }
    fun onFeedbackChange(value: String) { feedback = value }

    fun calcularPuntajeTotal(activities: List<Activity>): Int {
        return activities.sumOf { it.puntajeMaximo }
    }
    fun calcularPorcentajeAcierto(
        puntosObtenidos: Int,
        puntajeTotal: Int
    ): Double {

        if (puntajeTotal <= 0) return 0.0

        return (puntosObtenidos.toDouble() / puntajeTotal) * 100
    }
    fun calcularPuntosObtenidos(
        respuestasCorrectas: List<Activity>
    ): Int {
        return respuestasCorrectas.sumOf { it.puntajeMaximo }
    }
    fun actualizarPuntajeTotal(
        activities: List<Activity>
    ) {
        puntajeTotalActual =
            calcularPuntajeTotal(activities)
    }


    fun saveActivity(sessionId: String, onSuccess: () -> Unit) {
        if (enunciado.isBlank()) {
            errorMessage = "El enunciado no puede estar vacío."
            return
        }



        val puntaje = puntajeMaximo.toIntOrNull() ?: 0
        if (puntaje <= 0) {
            errorMessage = "El puntaje máximo debe ser un número mayor a 0."
            return
        }

        val valorPorcentaje = porcentaje.toIntOrNull() ?: 0
        if (valorPorcentaje < 0 || valorPorcentaje > 100) {
            errorMessage = "El porcentaje debe estar entre 0 y 100."
            return
        }

        val contenido = mutableMapOf<String, Any>()
        contenido["pregunta"] = enunciado

        when (selectedType) {
            "quiz" -> {
                if (options.any { it.isBlank() }) {
                    errorMessage = "Todas las opciones deben tener texto."
                    return
                }
                contenido["opciones"] = options.toList()
                contenido["respuesta_correcta"] = options[correctAnswerIndex]
            }
            "emparejamiento" -> {
                if (matchConcepts.any { it.isBlank() } || matchAnswers.any { it.isBlank() }) {
                    errorMessage = "Todos los conceptos y respuestas deben tener texto."
                    return
                }
                val pairs = matchConcepts.zip(matchAnswers).map { mapOf("concepto" to it.first, "respuesta" to it.second) }
                contenido["pares"] = pairs
            }
            "completar" -> {
                val regex = "\\[(.*?)\\]".toRegex()
                val match = regex.find(enunciado)
                if (match != null) {
                    val extracted = match.groupValues[1]
                    contenido["respuesta_esperada"] = extracted
                    contenido["pregunta"] = enunciado.replace("[$extracted]", "__________")
                } else {
                    errorMessage = "Debes indicar la respuesta entre corchetes [ ] dentro de la frase."
                    return
                }
            }
            "traduccion" -> {
                if (expectedAnswer.isBlank()) {
                    errorMessage = "La respuesta esperada no puede estar vacía."
                    return
                }
                contenido["respuesta_esperada"] = expectedAnswer
            }
        }

        // Guardar sugerencia de mejora si existe
        if (feedback.isNotBlank()) {
            contenido["feedback"] = feedback
        }

        isLoading = true
        errorMessage = null

        // Fetch current activities to get order
        sessionRepository.getActivitiesForSession(sessionId) { activities, getError ->
            if (getError != null) {
                isLoading = false
                errorMessage = "Error al obtener actividades: $getError"
                return@getActivitiesForSession
            }
            actualizarPuntajeTotal(activities)
            val puntajeTotalActual= calcularPuntajeTotal(activities)
            val nuevoPuntajeTotal = puntajeTotalActual + puntaje

            val newOrder = activities.size + 1
            
            val processedPregunta = contenido["pregunta"] as? String ?: enunciado
            
            val activity = Activity(
                titulo = if (selectedType == "quiz") "Pregunta de Quiz" else selectedType.replaceFirstChar { it.uppercase() },
                tipo = selectedType,
                descripcion = if (selectedType == "completar") processedPregunta else enunciado,
                cantidad = 1,
                orden = newOrder,
                contenido = contenido,
                puntajeMaximo = puntaje,
                porcentaje = valorPorcentaje,
                feedback = feedback
            )

            sessionRepository.addActivityToSession(sessionId, activity) { success, addError ->
                isLoading = false
                if (success) {
                    successMessage = "Actividad guardada correctamente"
                    onSuccess()
                } else {
                    errorMessage = addError ?: "Error desconocido al guardar la actividad"
                }
            }
        }
    }
}
