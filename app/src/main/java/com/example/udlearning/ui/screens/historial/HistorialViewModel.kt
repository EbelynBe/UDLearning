package com.example.udlearning.ui.screens.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.udlearning.data.HistorialRepository
import com.example.udlearning.data.model.HistorialEstudiante
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistorialViewModel : ViewModel() {
    private val repository = HistorialRepository()

    private val _historial = MutableStateFlow<List<HistorialEstudiante>>(emptyList())
    val historial: StateFlow<List<HistorialEstudiante>> = _historial

    fun loadHistorial(estudianteId: String) {
        viewModelScope.launch {
            repository.getHistorialEstudiante(estudianteId).collect {
                _historial.value = it
            }
        }
    }
}
