package com.example.udlearning.ui.screens.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.GroupRepository
import com.example.udlearning.data.SessionRepository
import com.example.udlearning.data.model.Group
import com.example.udlearning.data.model.Session
import com.google.firebase.Timestamp
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

class ManageSessionViewModel : ViewModel() {

    private val sessionRepository = SessionRepository()

    var session by mutableStateOf<Session?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set
        
    var title by mutableStateOf("")
        private set
    var topic by mutableStateOf("")
        private set
    var level by mutableStateOf("")
        private set
    var learningObjective by mutableStateOf("")
        private set
    var estado by mutableStateOf("pendiente")
        private set
    var startDate by mutableStateOf("")
        private set
    var endDate by mutableStateOf("")
        private set
    var availableGroups = mutableStateListOf<Group>()
        private set
    var selectedGroupIds = mutableStateListOf<String>()
        private set
        
    var actionSuccess by mutableStateOf<String?>(null)
        private set

    var isCreator by mutableStateOf(false)
        private set

    var showArchiveDialog by mutableStateOf(false)
    var showDeleteDialog by mutableStateOf(false)
        
    var participantCount by mutableStateOf(0)
        private set

    var accessRecords = mutableStateListOf<com.example.udlearning.data.model.SessionAccess>()
        private set

    fun loadStatistics() {
        val sessionId = session?.sessionId ?: return
        sessionRepository.getSessionAccessRecords(sessionId) { records, _ ->
            accessRecords.clear()
            accessRecords.addAll(records)
        }
    }
        
    fun onTitleChange(value: String) { title = value }
    fun onTopicChange(value: String) { topic = value }
    fun onLevelChange(value: String) { level = value }
    fun onLearningObjectiveChange(value: String) { learningObjective = value }
    fun onEstadoChange(value: String) { estado = value }
    fun onStartDateChange(value: String) { startDate = value }
    fun onEndDateChange(value: String) { endDate = value }
    
    /** Returns the epoch-millis (UTC+0 noon) for the stored startDate string, or null if empty/unparseable. */
    fun startDateMillis(): Long? = parseDateToUtcMillis(startDate)
    /** Returns the epoch-millis (UTC+0 noon) for the stored endDate string, or null if empty/unparseable. */
    fun endDateMillis(): Long? = parseDateToUtcMillis(endDate)

    private fun parseDateToUtcMillis(dateStr: String): Long? {
        if (dateStr.isEmpty()) return null
        return try {
            val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parsed = fmt.parse(dateStr) ?: return null
            // Add timezone offset back so the picker shows the correct local day
            val offset = java.util.TimeZone.getDefault().getOffset(parsed.time).toLong()
            parsed.time + offset
        } catch (e: Exception) { null }
    }
    fun toggleGroupSelection(groupId: String, isSelected: Boolean) {
        if (isSelected) { if (!selectedGroupIds.contains(groupId)) selectedGroupIds.add(groupId) }
        else selectedGroupIds.remove(groupId)
    }

    fun loadSession(sessionId: String) {
        isLoading = true
        // Load groups in parallel
        GroupRepository().getGroups { groups, _ ->
            availableGroups.clear()
            availableGroups.addAll(groups)
        }
        sessionRepository.getSession(sessionId) { resultSession, error ->
            isLoading = false
            if (error != null) {
                errorMessage = error
            } else {
                session = resultSession
                resultSession?.let {
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                    isCreator = it.creadoPor == currentUserId

                    title = it.titulo
                    topic = it.tema
                    level = it.nivel
                    learningObjective = it.objetivo
                    estado = it.estado
                    // Convert timestamps to display strings
                    val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    startDate = it.fechaInicio?.toDate()?.let { d -> fmt.format(d) } ?: ""
                    endDate = it.fechaFin?.toDate()?.let { d -> fmt.format(d) } ?: ""
                    selectedGroupIds.clear()
                    selectedGroupIds.addAll(it.grupos)
                    loadStatistics()
                }
            }
        }
    }

    fun prepareDelete() {
        val currentSessionId = session?.sessionId ?: return
        isLoading = true
        sessionRepository.getParticipantCount(currentSessionId) { count, error ->
            isLoading = false
            if (error != null) {
                errorMessage = error
            } else {
                participantCount = count
                showDeleteDialog = true
            }
        }
    }

    fun archiveSession(onSuccess: () -> Unit) {
        val currentSessionId = session?.sessionId ?: return
        isLoading = true
        sessionRepository.updateSessionStatus(currentSessionId, "archivada") { success, error ->
            isLoading = false
            if (success) {
                actionSuccess = "Sesión archivada con éxito"
                onSuccess()
            } else {
                errorMessage = error
            }
        }
    }

    fun deleteSession(onSuccess: () -> Unit) {
        val currentSessionId = session?.sessionId ?: return
        isLoading = true
        sessionRepository.deleteSession(currentSessionId) { success, error ->
            isLoading = false
            if (success) {
                actionSuccess = "Sesión eliminada con éxito"
                onSuccess()
            } else {
                errorMessage = error
            }
        }
    }

    fun updateSession(onSuccess: () -> Unit) {
        val currentSession = session ?: return

        if (title.isEmpty() || topic.isEmpty() || level.isEmpty() || learningObjective.isEmpty()) {
            errorMessage = "Complete todos los campos obligatorios."
            return
        }
        if (startDate.isEmpty() || endDate.isEmpty()) {
            errorMessage = "Seleccione las fechas de inicio y fin."
            return
        }
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startParsed = try { formatter.parse(startDate) } catch(e: Exception) { null }
        val endParsed = try { formatter.parse(endDate) } catch(e: Exception) { null }
        if (startParsed == null || endParsed == null) {
            errorMessage = "Formato de fecha inválido."
            return
        }
        if (startParsed.after(endParsed)) {
            errorMessage = "La fecha de inicio no puede ser posterior a la fecha de finalización."
            return
        }

        isLoading = true
        errorMessage = null

        val updatedSession = currentSession.copy(
            titulo = title,
            tema = topic,
            nivel = level,
            objetivo = learningObjective,
            estado = estado,
            fechaInicio = Timestamp(startParsed),
            fechaFin = Timestamp(endParsed),
            grupos = selectedGroupIds.toList()
        )

        sessionRepository.updateSessionFull(updatedSession) { success, error ->
            isLoading = false
            if (success) {
                actionSuccess = "Sesión actualizada"
                session = updatedSession
                onSuccess()
            } else {
                errorMessage = error
            }
        }
    }
}
