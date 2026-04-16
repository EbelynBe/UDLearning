package com.example.udlearning.ui.screens.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.udlearning.data.GroupRepository
import com.example.udlearning.data.SessionRepository
import com.example.udlearning.data.model.Activity
import com.example.udlearning.data.model.Group
import com.example.udlearning.data.model.Session
import com.google.firebase.Timestamp
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

class SessionViewModel : ViewModel() {

    private val repository = SessionRepository()
    private val groupRepository = GroupRepository()

    // Screen 1: Create Session fields
    var title by mutableStateOf("")
        private set
    var topic by mutableStateOf("")
        private set
    var level by mutableStateOf("")
        private set
    var learningObjective by mutableStateOf("")
        private set
    var activityType by mutableStateOf("")
        private set

    // Screen 2: Schedule Session fields
    var availableGroups = mutableStateListOf<Group>()
        private set

    var selectedGroupIds = mutableStateListOf<String>()
        private set
    
    // In a real app we'd use Calendar/Date instances to generate a Timestamp
    var startDate by mutableStateOf("")
        private set
    var endDate by mutableStateOf("")
        private set
        
    // UI states
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadGroups()
    }

    private fun loadGroups() {
        groupRepository.getGroups { groups, _ ->
            availableGroups.clear()
            availableGroups.addAll(groups)
        }
    }

    // Field Updaters
    fun onTitleChange(value: String) { title = value }
    fun onTopicChange(value: String) { topic = value }
    fun onLevelChange(value: String) { level = value }
    fun onLearningObjectiveChange(value: String) { learningObjective = value }
    fun onActivityTypeChange(value: String) { activityType = value }
    
    fun toggleGroupSelection(groupId: String, isSelected: Boolean) {
        if (isSelected) {
            if (!selectedGroupIds.contains(groupId)) selectedGroupIds.add(groupId)
        } else {
            selectedGroupIds.remove(groupId)
        }
    }
    
    fun onStartDateChange(value: String) { startDate = value }
    fun onEndDateChange(value: String) { endDate = value }

    fun validateCreateSession(): Boolean {
        if (title.isEmpty() || topic.isEmpty() || level.isEmpty() || learningObjective.isEmpty() || activityType.isEmpty()) {
            errorMessage = "Complete todos los campos obligatorios."
            return false
        }
        errorMessage = null
        return true
    }

    fun saveSessionWithSchedule(onSuccess: () -> Unit) {
        if (!validateCreateSession()) return
        
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

        val newSession = Session(
            titulo = title,
            tema = topic,
            nivel = level,
            objetivo = learningObjective,
            tipoActividad = activityType,
            estado = "pendiente",
            grupos = selectedGroupIds.toList(),
            fechaInicio = Timestamp(startParsed), 
            fechaFin = Timestamp(endParsed),
            creadoPor = FirebaseAuth.getInstance().currentUser?.uid ?: "currentUser"
        )
        
        // Let's create an Activity based on user's inputted type as a mock or based on the type string
        val activities = listOf(
            Activity(
                titulo = activityType,
                tipo = "completar", // Sample default
                descripcion = learningObjective,
                cantidad = 5,
                orden = 1
            )
        )

        repository.createSession(newSession, activities) { success, error ->
            isLoading = false
            if (success) {
                onSuccess()
            } else {
                errorMessage = error
            }
        }
    }
    
    fun clearState() {
        title = ""
        topic = ""
        level = ""
        learningObjective = ""
        activityType = ""
        selectedGroupIds.clear()
        startDate = ""
        endDate = ""
    }
}
