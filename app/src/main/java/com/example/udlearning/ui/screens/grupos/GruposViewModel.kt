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

    fun assignTeacherToGroup(group: Group, teacherId: String) {
        val updatedGroup = group.copy(docenteId = teacherId)
        groupRepository.updateGroup(updatedGroup) { success, _ ->
            if (success) {
                loadGrupos() // Reload to reflect changes
            }
        }
    }
}
