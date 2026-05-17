package com.example.udlearning.ui.screens.review

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.SessionRepository
import com.example.udlearning.data.model.Session
import com.example.udlearning.data.model.SessionAccess
import com.google.firebase.auth.FirebaseAuth

class ReviewSessionViewModel : ViewModel() {

    private val sessionRepository = SessionRepository()

    var session by mutableStateOf<Session?>(null)
        private set

    var sessionAccess by mutableStateOf<SessionAccess?>(null)
        private set

    var activities by mutableStateOf<List<com.example.udlearning.data.model.Activity>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadReview(sessionId: String) {
        isLoading = true
        errorMessage = null
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            errorMessage = "No se encontró el usuario actual."
            isLoading = false
            return
        }

        sessionRepository.getSession(sessionId) { sess, sessError ->
            if (sess == null) {
                errorMessage = sessError ?: "Sesión no encontrada."
                isLoading = false
                return@getSession
            }
            session = sess

            sessionRepository.getActivitiesForSession(sessionId) { acts, actsError ->
                activities = acts
                
                sessionRepository.getSessionAccessRecords(sessionId) { records, recError ->
                    isLoading = false
                    if (recError != null) {
                        errorMessage = recError
                    } else {
                        // Buscar el registro más reciente del usuario que esté completado
                        val userRecord = records
                            .filter { it.userId == currentUser.uid && it.estado == "completada" }
                            .sortedByDescending { it.fechaCompletado }
                            .firstOrNull()

                        if (userRecord == null) {
                            errorMessage = "No se encontró un intento completado para esta evaluación."
                        } else {
                            sessionAccess = userRecord
                        }
                    }
                }
            }
        }
    }
}
