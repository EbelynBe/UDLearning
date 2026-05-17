package com.example.udlearning.ui.screens.review

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.udlearning.ui.components.TitleHeader
import com.example.udlearning.ui.theme.color.*

@Composable
fun ReviewSessionScreen(
    sessionId: String,
    onNavigateBack: () -> Unit,
    viewModel: ReviewSessionViewModel = viewModel()
) {
    LaunchedEffect(sessionId) {
        viewModel.loadReview(sessionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        TitleHeader(onBackClick = onNavigateBack)

        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Revisión de Evaluación",
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = White)
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = White,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                val access = viewModel.sessionAccess
                val session = viewModel.session
                
                if (access != null && session != null) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = White.copy(alpha = 0.15f)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, White.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = session.titulo,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = YellowText
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tu calificación final",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "${access.puntaje} / ${access.totalPosible}",
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Black,
                                        color = White
                                    )
                                }
                            }
                        }

                        items(access.respuestas) { answer ->
                            val activity = viewModel.activities.find { it.activityId == answer.activityId }
                            ReviewAnswerItem(answer, activity)
                        }

                        if (access.respuestas.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        "No hay detalles de respuestas disponibles para este intento.",
                                        textAlign = TextAlign.Center,
                                        fontSize = 14.sp,
                                        color = White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewAnswerItem(
    answer: com.example.udlearning.data.model.UserAnswer,
    activity: com.example.udlearning.data.model.Activity?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (activity != null) {
                Text(
                    text = activity.titulo,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGrayText
                )
                if (activity.descripcion.isNotBlank()) {
                    Text(
                        text = activity.descripcion,
                        fontSize = 13.sp,
                        color = DarkGrayText.copy(alpha = 0.7f),
                        lineHeight = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (answer.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (answer.isCorrect) GreenTag else RedPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (answer.isCorrect) "¡Correcto!" else "Incorrecto",
                    fontWeight = FontWeight.Bold,
                    color = if (answer.isCorrect) GreenTag else RedPrimary,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // User Answer Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GrayField.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(text = "Tu respuesta:", style = MaterialTheme.typography.labelSmall, color = DarkGrayText.copy(alpha = 0.5f))
                Text(
                    text = formatAnswer(answer.userAnswer),
                    fontSize = 15.sp,
                    color = DarkGrayText,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Correct Answer Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenTag.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                    .border(1.dp, GreenTag.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(text = "Respuesta correcta:", style = MaterialTheme.typography.labelSmall, color = GreenTag)
                Text(
                    text = formatAnswer(answer.correctAnswer),
                    fontSize = 15.sp,
                    color = DarkGrayText,
                    fontWeight = FontWeight.Bold
                )
            }

            // Feedback Section
            val feedback = activity?.feedback ?: (activity?.contenido?.get("feedback") as? String) ?: ""
            if (!answer.isCorrect && feedback.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(OrangeTag.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .border(1.dp, OrangeTag.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "💡 Sugerencia del docente",
                        style = MaterialTheme.typography.labelSmall,
                        color = OrangeTag,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = feedback,
                        fontSize = 14.sp,
                        color = DarkGrayText
                    )
                }
            }
        }
    }
}

fun formatAnswer(answer: Any?): String {
    return when (answer) {
        is String -> if (answer.isBlank()) "Sin respuesta" else answer
        is List<*> -> answer.joinToString(", ")
        is Map<*, *> -> answer.entries.joinToString("\n") { "${it.key} → ${it.value}" }
        else -> answer?.toString() ?: "Sin respuesta"
    }
}
