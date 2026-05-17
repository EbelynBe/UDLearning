package com.example.udlearning.ui.screens.assessment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.SessionRepository
import com.example.udlearning.data.UserRepository
import com.example.udlearning.data.model.Session
import com.google.firebase.auth.FirebaseAuth

class StudentAssessmentsViewModel : ViewModel() {

    private val sessionRepository = SessionRepository()
    private val userRepository = UserRepository()

    var sessions by mutableStateOf<List<Session>>(emptyList())
        private set
        
    var filteredSessions by mutableStateOf<List<Session>>(emptyList())
        private set

    var currentFilter by mutableStateOf("Todas")
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchStudentAssessments()
    }

    fun fetchStudentAssessments() {
        isLoading = true
        errorMessage = null
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            errorMessage = "No se encontró el usuario actual"
            isLoading = false
            return
        }

        userRepository.getUser(currentUser.uid) { user, error ->
            if (user?.groupId != null) {
                sessionRepository.getStudentSessions(user.groupId) { resultList, fetchError ->
                    isLoading = false
                    if (fetchError != null) {
                        errorMessage = fetchError
                    } else {
                        // Solo evaluaciones
                        sessions = resultList.filter { it.isEvaluation }
                        applyFilter(currentFilter)
                    }
                }
            } else {
                isLoading = false
                errorMessage = error ?: "No se encontró el grupo del estudiante"
            }
        }
    }

    fun applyFilter(filter: String) {
        currentFilter = filter
        filteredSessions = when (filter) {
            "Todas" -> sessions
            "Activas" -> sessions.filter { it.estado == "activa" }
            "Pendientes" -> sessions.filter { it.estado == "pendiente" }
            "Próximas" -> sessions.filter { it.estado == "proxima" }
            "Finalizadas" -> sessions.filter { it.estado == "finalizada" }
            else -> sessions
        }
    }
}
