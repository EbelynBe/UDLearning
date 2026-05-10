package com.example.udlearning.ui.screens.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.SessionRepository
import com.example.udlearning.data.model.Activity

class EditActivityViewModel : ViewModel() {

    private val sessionRepository = SessionRepository()

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    var originalOrder by mutableStateOf(0)
        private set

    var selectedType by mutableStateOf("quiz")
        private set

    var enunciado by mutableStateOf("")
        private set
    var puntajeMaximo by mutableStateOf("1")
        private set

    var options = mutableStateListOf("Opción 1", "Opción 2")
        private set
    var correctAnswerIndex by mutableStateOf(0)
        private set

    var matchConcepts = mutableStateListOf("Concepto 1", "Concepto 2")
        private set
    var matchAnswers = mutableStateListOf("Respuesta 1", "Respuesta 2")
        private set

    var expectedAnswer by mutableStateOf("")
        private set

    var feedback by mutableStateOf("")
        private set

    fun loadActivity(sessionId: String, activityId: String) {
        isLoading = true
        errorMessage = null

        sessionRepository.getActivity(sessionId, activityId) { activity, error ->
            isLoading = false
            if (activity == null) {
                errorMessage = error ?: "No se pudo cargar la actividad."
                return@getActivity
            }

            // Populate base data
            originalOrder = activity.orden
            selectedType = activity.tipo.ifBlank { "quiz" }
            enunciado = activity.descripcion
            puntajeMaximo = activity.puntajeMaximo.toString()

            val contenido = activity.contenido

            // Populate specific fields based on type
            when (selectedType) {
                "quiz" -> {
                    val opts = contenido["opciones"] as? List<String>
                    if (opts != null && opts.isNotEmpty()) {
                        options.clear()
                        options.addAll(opts)
                    }
                    val correct = contenido["respuesta_correcta"] as? String
                    correctAnswerIndex = options.indexOf(correct).takeIf { it >= 0 } ?: 0
                }
                "emparejamiento" -> {
                    val pares = contenido["pares"] as? List<Map<String, String>>
                    if (pares != null && pares.isNotEmpty()) {
                        matchConcepts.clear()
                        matchAnswers.clear()
                        pares.forEach { par ->
                            matchConcepts.add(par["concepto"] ?: "")
                            matchAnswers.add(par["respuesta"] ?: "")
                        }
                    }
                }
                "completar", "traduccion" -> {
                    expectedAnswer = contenido["respuesta_esperada"] as? String ?: ""
                }
            }

            // Cargar feedback si existe
            feedback = contenido["feedback"] as? String ?: ""
        }
    }

    fun onTypeSelected(type: String) {
        selectedType = type
        errorMessage = null
    }

    fun onEnunciadoChange(value: String) { enunciado = value }
    fun onPuntajeChange(value: String) { puntajeMaximo = value }

    fun updateOption(index: Int, value: String) {
        if (index in options.indices) options[index] = value
    }
    fun addOption() { options.add("Nueva opción") }
    fun removeOption(index: Int) {
        if (options.size > 2) {
            options.removeAt(index)
            if (correctAnswerIndex >= options.size) correctAnswerIndex = options.size - 1
        }
    }
    fun selectCorrectAnswer(index: Int) { correctAnswerIndex = index }

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

    fun onExpectedAnswerChange(value: String) { expectedAnswer = value }
    fun onFeedbackChange(value: String) { feedback = value }

    fun updateActivity(sessionId: String, activityId: String, onSuccess: () -> Unit) {
        if (enunciado.isBlank()) {
            errorMessage = "El enunciado no puede estar vacío."
            return
        }

        val puntaje = puntajeMaximo.toIntOrNull() ?: 0
        if (puntaje <= 0) {
            errorMessage = "El puntaje máximo debe ser mayor a 0."
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
            "completar", "traduccion" -> {
                if (expectedAnswer.isBlank()) {
                    errorMessage = "La respuesta esperada no puede estar vacía."
                    return
                }
                contenido["respuesta_esperada"] = expectedAnswer
            }
        }

        // Guardar feedback si existe
        if (feedback.isNotBlank()) {
            contenido["feedback"] = feedback
        }

        isLoading = true
        errorMessage = null

        val activity = Activity(
            activityId = activityId,
            titulo = if (selectedType == "quiz") "Pregunta de Quiz" else selectedType.replaceFirstChar { it.uppercase() },
            tipo = selectedType,
            descripcion = enunciado,
            cantidad = 1,
            orden = originalOrder, // Preservar el orden original
            contenido = contenido,
            puntajeMaximo = puntaje
        )

        sessionRepository.addActivityToSession(sessionId, activity) { success, error ->
            isLoading = false
            if (success) {
                successMessage = "Actividad actualizada correctamente"
                onSuccess()
            } else {
                errorMessage = error ?: "Error al actualizar la actividad"
            }
        }
    }
}
