package com.example.udlearning.ui.screens.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.SessionRepository
import com.example.udlearning.data.UserRepository
import com.example.udlearning.data.model.Activity
import com.example.udlearning.data.model.SessionAccess
import com.example.udlearning.data.model.UserHistory
import com.google.firebase.auth.FirebaseAuth

class SolveSessionViewModel : ViewModel() {

    private val sessionRepository = SessionRepository()
    private val userRepository = UserRepository()

    var activities by mutableStateOf<List<Activity>>(emptyList())
        private set

    var currentIndex by mutableStateOf(0)
        private set

    var score by mutableStateOf(0)
        private set

    var totalPossibleScore by mutableStateOf(0)
        private set

    var isFinished by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var currentAccessId: String? = null
    private var currentSessionId: String = ""
    private var sessionTitle: String = ""

    fun loadActivities(sessionId: String) {
        isLoading = true
        errorMessage = null
        currentIndex = 0
        score = 0
        isFinished = false
        currentSessionId = sessionId

        val currentUser = FirebaseAuth.getInstance().currentUser

        sessionRepository.getActivitiesForSession(sessionId) { resultList, error ->
            if (error != null) {
                errorMessage = error
                isLoading = false
                return@getActivitiesForSession
            }
            if (resultList.isEmpty()) {
                errorMessage = "Esta sesión no tiene actividades."
                isLoading = false
                return@getActivitiesForSession
            }
            activities = resultList.sortedBy { it.orden }
            totalPossibleScore = activities.sumOf { it.puntajeMaximo }

            // Get session title for history
            sessionRepository.getSession(sessionId) { session, _ ->
                sessionTitle = session?.titulo ?: "Sesión"
                
                // Register access start
                if (currentUser != null) {
                    userRepository.getUser(currentUser.uid) { user, _ ->
                        val access = SessionAccess(
                            sessionId = sessionId,
                            userId = currentUser.uid,
                            userName = user?.nombre ?: "Estudiante",
                            totalPosible = totalPossibleScore
                        )
                        sessionRepository.startSessionAccess(access) { accessId ->
                            currentAccessId = accessId
                            isLoading = false
                        }
                    }
                } else {
                    isLoading = false
                }
            }
        }
    }

    fun verifyStringAnswer(input: String, expected: String): Boolean {
        return input.trim().equals(expected.trim(), ignoreCase = true)
    }

    fun getCorrectAnswerText(activity: Activity): String {
        return when (activity.tipo) {
            "quiz" -> activity.contenido["respuesta_correcta"] as? String ?: ""
            "completar", "traduccion" -> activity.contenido["respuesta_esperada"] as? String ?: ""
            "emparejamiento" -> {
                @Suppress("UNCHECKED_CAST")
                val pares = activity.contenido["pares"] as? List<Map<String, String>> ?: emptyList()
                pares.joinToString("\n") { "${it["concepto"]} → ${it["respuesta"]}" }
            }
            else -> ""
        }
    }

    fun moveToNext(wasCorrect: Boolean) {
        val currentActivity = activities.getOrNull(currentIndex)
        if (currentActivity != null && wasCorrect) {
            score += currentActivity.puntajeMaximo
        }

        if (currentIndex < activities.size - 1) {
            currentIndex++
        } else {
            isFinished = true
            finalizeSession()
        }
    }

    private fun finalizeSession() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val accessId = currentAccessId ?: return

        // 1. Update acceso_sesion
        sessionRepository.completeSessionAccess(accessId, score) { }

        // 2. Save user history
        val history = UserHistory(
            sessionId = currentSessionId,
            tema = sessionTitle,
            puntaje = score,
            totalPosible = totalPossibleScore
        )
        // Note: the sessionId is in history, but we need it from the load call. 
        // Let's pass it or store it.
        
        sessionRepository.saveUserHistory(currentUser.uid, history) { }
    }
}
