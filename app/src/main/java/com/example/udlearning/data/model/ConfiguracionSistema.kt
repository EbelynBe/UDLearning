package com.example.udlearning.data.model

data class ConfiguracionSistema(
    val id: String = "",
    val parametro: String = "",
    val valor: String = "",
    val descripcion: String = "",
    val rolAplicable: String = "",
    val fechaModificacion: Long = 0L
)
