package com.example.udlearning.data.model

data class UserAnswer(
    val activityId: String = "",
    val activityType: String = "",
    val userAnswer: Any? = null,
    val isCorrect: Boolean = false,
    val correctAnswer: Any? = null
)
