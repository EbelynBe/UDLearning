package com.example.udlearning.ui.screens.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.SessionRepository
import com.example.udlearning.data.UserRepository
import com.example.udlearning.data.model.Activity
import com.example.udlearning.data.model.Participant
import com.example.udlearning.data.model.Session
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

class SessionActivitiesViewModel : ViewModel() {

    private val sessionRepository = SessionRepository()
    private val userRepository = UserRepository()

    var session by mutableStateOf<Session?>(null)
        private set

    var activities by mutableStateOf<List<Activity>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    // null = no error | string = error/info message to show (blocks access)
    var accessError by mutableStateOf<String?>(null)
        private set

    fun loadSession(sessionId: String) {
        isLoading = true
        accessError = null

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            accessError = "No se encontró el usuario actual."
            isLoading = false
            return
        }

        // Step 1: get student profile (need groupId)
        userRepository.getUser(currentUser.uid) { user, userError ->
            if (user == null) {
                accessError = userError ?: "No se pudo obtener el perfil del estudiante."
                isLoading = false
                return@getUser
            }
            val studentGroupId = user.groupId ?: ""

            // Step 2: get session details
            sessionRepository.getSession(sessionId) { sess, sessError ->
                if (sess == null) {
                    accessError = sessError ?: "La sesión no existe."
                    isLoading = false
                    return@getSession
                }

                session = sess

                // --- Validation 1: estado must be "activa" ---
                if (sess.estado != "activa") {
                    accessError = when (sess.estado) {
                        "pendiente"  -> "⏳ Esta sesión aún no ha iniciado."
                        "finalizada" -> "✅ Esta sesión ya ha finalizado."
                        "archivada"  -> "📂 Esta sesión ha sido archivada."
                        else         -> "Esta sesión no está disponible."
                    }
                    isLoading = false
                    return@getSession
                }

                // --- Validation 2: availability period ---
                val now = Date()
                val start = sess.fechaInicio?.toDate()
                val end   = sess.fechaFin?.toDate()
                if (start != null && now.before(start)) {
                    accessError = "⏳ Esta sesión aún no ha iniciado (disponible desde ${formatDate(start)})."
                    isLoading = false
                    return@getSession
                }
                if (end != null && now.after(end)) {
                    accessError = "✅ El período de esta sesión ha finalizado (terminó el ${formatDate(end)})."
                    isLoading = false
                    return@getSession
                }

                // --- Validation 3: student must belong to an assigned group ---
                if (studentGroupId.isEmpty() || !sess.grupos.contains(studentGroupId)) {
                    accessError = "🚫 No estás asignado al grupo de esta sesión."
                    isLoading = false
                    return@getSession
                }

                // --- All checks passed: load activities ---
                sessionRepository.getActivitiesForSession(sessionId) { resultList, actError ->
                    if (actError != null) {
                        accessError = actError
                        isLoading = false
                        return@getActivitiesForSession
                    }

                    activities = resultList.sortedBy { it.orden }

                    // --- Register session start in participant history ---
                    val participant = Participant(
                        userId = currentUser.uid,
                        groupId = studentGroupId,
                        estado = "activo",
                        fechaIngreso = Timestamp.now()
                    )
                    sessionRepository.addParticipantToSession(sessionId, participant) { _, _ -> }

                    isLoading = false
                }
            }
        }
    }

    private fun formatDate(date: Date): String {
        val fmt = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        return fmt.format(date)
    }
}
