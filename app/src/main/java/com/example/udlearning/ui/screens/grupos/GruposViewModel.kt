package com.example.udlearning.ui.screens.grupos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udlearning.data.GroupRepository
import com.example.udlearning.data.UserRepository
import com.example.udlearning.data.model.Group
import com.example.udlearning.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GruposViewModel : ViewModel() {
    private val groupRepository = GroupRepository()
    private val userRepository = UserRepository()

    private val _grupos = MutableStateFlow<List<Group>>(emptyList())
    val grupos: StateFlow<List<Group>> = _grupos

    private val _teachers = MutableStateFlow<List<User>>(emptyList())
    val teachers: StateFlow<List<User>> = _teachers

    private val _students = MutableStateFlow<List<User>>(emptyList())
    val students: StateFlow<List<User>> = _students

    // Create group form state
    private val _createNombre = MutableStateFlow("")
    val createNombre: StateFlow<String> = _createNombre

    private val _createNivel = MutableStateFlow("")
    val createNivel: StateFlow<String> = _createNivel

    private val _createSemestre = MutableStateFlow("")
    val createSemestre: StateFlow<String> = _createSemestre

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    fun onNombreChange(value: String) { _createNombre.value = value }
    fun onNivelChange(value: String) { _createNivel.value = value }
    fun onSemestreChange(value: String) { _createSemestre.value = value }
    fun clearMensaje() { _mensaje.value = null }

    fun loadGrupos() {
        groupRepository.getGroups { groups, _ ->
            _grupos.value = groups
        }
    }

    fun loadTeachers() {
        userRepository.getTeachers { teachersList, _ ->
            _teachers.value = teachersList
        }
    }

    fun loadStudents() {
        userRepository.getStudents { studentsList, _ ->
            _students.value = studentsList
        }
    }

    fun createGroup() {
        val nombre = _createNombre.value.trim()
        val nivel = _createNivel.value.trim()
        val semestre = _createSemestre.value.trim()

        if (nombre.isEmpty()) {
            _mensaje.value = "El nombre del grupo no puede estar vacío"
            return
        }
        if (semestre.isEmpty()) {
            _mensaje.value = "El semestre no puede estar vacío"
            return
        }

        val newGroup = Group(
            nombre = nombre,
            nivel = nivel,
            semestre = semestre
        )
        groupRepository.createGroup(newGroup) { success, error ->
            if (success) {
                _mensaje.value = "Grupo creado exitosamente"
                _createNombre.value = ""
                _createNivel.value = ""
                _createSemestre.value = ""
                loadGrupos()
            } else {
                _mensaje.value = "Error al crear grupo: $error"
            }
        }
    }

    fun updateGroup(group: Group, newNombre: String, newSemestre: String) {
        val updatedGroup = group.copy(
            nombre = newNombre.trim(),
            semestre = newSemestre.trim()
        )
        groupRepository.updateGroup(updatedGroup) { success, error ->
            if (success) {
                _mensaje.value = "Grupo actualizado exitosamente"
                loadGrupos()
            } else {
                _mensaje.value = "Error al actualizar: $error"
            }
        }
    }

    fun deleteGroup(groupId: String) {
        groupRepository.deleteGroup(groupId) { success, error ->
            if (success) {
                _mensaje.value = "Grupo eliminado"
                loadGrupos()
            } else {
                _mensaje.value = "Error al eliminar: $error"
            }
        }
    }

    fun assignTeacherToGroup(group: Group, teacherId: String) {
        val updatedGroup = group.copy(docenteId = teacherId)
        groupRepository.updateGroup(updatedGroup) { success, _ ->
            if (success) {
                _mensaje.value = "Docente asignado exitosamente"
                loadGrupos()
            }
        }
    }

    fun assignStudentToGroup(student: User, groupId: String) {
        // Update the user's groupId
        val updatedUser = student.copy(groupId = groupId)
        userRepository.updateUser(updatedUser) { success, _ ->
            if (success) {
                // Also update the group's estudianteIds list
                val group = _grupos.value.find { it.groupId == groupId }
                if (group != null) {
                    val updatedIds = if (student.userId in group.estudianteIds) {
                        group.estudianteIds - student.userId // Remove if already assigned (toggle)
                    } else {
                        group.estudianteIds + student.userId
                    }
                    val updatedGroup = group.copy(estudianteIds = updatedIds)
                    groupRepository.updateGroup(updatedGroup) { _, _ ->
                        loadGrupos()
                        loadStudents()
                    }
                }
            }
        }
    }

    fun removeStudentFromGroup(student: User, groupId: String) {
        val updatedUser = student.copy(groupId = null)
        userRepository.updateUser(updatedUser) { success, _ ->
            if (success) {
                val group = _grupos.value.find { it.groupId == groupId }
                if (group != null) {
                    val updatedGroup = group.copy(estudianteIds = group.estudianteIds - student.userId)
                    groupRepository.updateGroup(updatedGroup) { _, _ ->
                        _mensaje.value = "Estudiante removido del grupo"
                        loadGrupos()
                        loadStudents()
                    }
                }
            }
        }
    }
}
