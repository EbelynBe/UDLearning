package com.example.udlearning.ui.screens.configuracion

import androidx.lifecycle.ViewModel
import com.example.udlearning.data.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConfiguracionViewModel : ViewModel() {
    private val adminRepository = AdminRepository()

    private val _horarioLimite = MutableStateFlow("")
    val horarioLimite: StateFlow<String> = _horarioLimite

    private val _tiempoLimite = MutableStateFlow("")
    val tiempoLimite: StateFlow<String> = _tiempoLimite

    private val _alertasActivas = MutableStateFlow(true)
    val alertasActivas: StateFlow<Boolean> = _alertasActivas

    private val _permisosRol = MutableStateFlow("")
    val permisosRol: StateFlow<String> = _permisosRol

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _configLoaded = MutableStateFlow(false)
    val configLoaded: StateFlow<Boolean> = _configLoaded

    // Validation states
    private val _horarioError = MutableStateFlow<String?>(null)
    val horarioError: StateFlow<String?> = _horarioError

    private val _tiempoError = MutableStateFlow<String?>(null)
    val tiempoError: StateFlow<String?> = _tiempoError

    fun onHorarioChange(value: String) {
        _horarioLimite.value = value
        validateHorario(value)
    }

    fun onTiempoChange(value: String) {
        _tiempoLimite.value = value
        validateTiempo(value)
    }

    fun onAlertasChange(enabled: Boolean) {
        _alertasActivas.value = enabled
        // Save immediately when toggled
        adminRepository.updateAlertasActivas(enabled) { success, error ->
            if (success) {
                _mensaje.value = if (enabled) "Alertas activadas" else "Alertas desactivadas"
            } else {
                _mensaje.value = "Error al actualizar alertas: $error"
            }
        }
    }

    fun onPermisosChange(value: String) {
        _permisosRol.value = value
    }

    fun clearMensaje() { _mensaje.value = null }

    private fun validateHorario(value: String): Boolean {
        if (value.isEmpty()) {
            _horarioError.value = null
            return true
        }
        val regex = Regex("^([01]?\\d|2[0-3]):[0-5]\\d$")
        return if (!regex.matches(value)) {
            _horarioError.value = "Formato inválido. Use HH:MM (ej: 23:59)"
            false
        } else {
            _horarioError.value = null
            true
        }
    }

    private fun validateTiempo(value: String): Boolean {
        if (value.isEmpty()) {
            _tiempoError.value = null
            return true
        }
        val num = value.toIntOrNull()
        return if (num == null || num < 1 || num > 180) {
            _tiempoError.value = "Debe ser un número entre 1 y 180 minutos"
            false
        } else {
            _tiempoError.value = null
            true
        }
    }

    fun loadConfiguracion() {
        _isLoading.value = true
        adminRepository.loadConfiguracion { data, error ->
            _isLoading.value = false
            if (data != null) {
                _horarioLimite.value = (data["horarioLimite"] as? String) ?: "23:59"
                _tiempoLimite.value = (data["tiempoLimiteEvaluacion"] as? String) ?: "60"
                _alertasActivas.value = (data["alertasActivas"] as? Boolean) ?: true
                _permisosRol.value = (data["permisosRol"] as? String) ?: ""
                _configLoaded.value = true
            } else {
                _mensaje.value = "Error al cargar configuración: $error"
            }
        }
    }

    fun saveConfiguracion() {
        val horarioValid = validateHorario(_horarioLimite.value)
        val tiempoValid = validateTiempo(_tiempoLimite.value)

        if (!horarioValid || !tiempoValid) {
            _mensaje.value = "Corrige los errores antes de guardar"
            return
        }

        _isLoading.value = true
        adminRepository.saveConfiguracion(
            horarioLimite = _horarioLimite.value,
            tiempoLimiteEvaluacion = _tiempoLimite.value,
            alertasActivas = _alertasActivas.value,
            permisosRol = _permisosRol.value
        ) { success, error ->
            _isLoading.value = false
            if (success) {
                _mensaje.value = "Configuración guardada exitosamente"
            } else {
                _mensaje.value = "Error al guardar: $error"
            }
        }
    }
}
