package com.example.udlearning.ui.screens.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.SessionRepository
import com.example.udlearning.data.model.Session
import com.google.firebase.auth.FirebaseAuth

class TeacherSessionsViewModel : ViewModel() {

    private val sessionRepository = SessionRepository()

    var sessions by mutableStateOf<List<Session>>(emptyList())
        private set
        
    var filteredSessions by mutableStateOf<List<Session>>(emptyList())
        private set

    var currentFilter by mutableStateOf("Todas")
        private set

    var showEvaluations by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchTeacherSessions()
    }

    fun fetchTeacherSessions() {
        isLoading = true
        errorMessage = null
        
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            errorMessage = "No se encontró el usuario actual"
            isLoading = false
            return
        }

        sessionRepository.getTeacherSessions(currentUser.uid) { resultList, fetchError ->
            isLoading = false
            if (fetchError != null) {
                errorMessage = fetchError
            } else {
                // Solo sesiones normales
                sessions = resultList.filter { !it.isEvaluation }
                applyFilter(currentFilter)
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
