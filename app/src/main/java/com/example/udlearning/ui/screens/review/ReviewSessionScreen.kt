package com.example.udlearning.ui.screens.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewSessionScreen(
    sessionId: String,
    onNavigateBack: () -> Unit,
    viewModel: ReviewSessionViewModel = viewModel()
) {
    LaunchedEffect(sessionId) {
        viewModel.loadReview(sessionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Revisión de Evaluación") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else {
                val access = viewModel.sessionAccess
                val session = viewModel.session
                
                if (access != null && session != null) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = session.titulo,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Resultado: ${access.puntaje} / ${access.totalPosible}")
                                }
                            }
                        }

                        items(access.respuestas) { answer ->
                            ReviewAnswerItem(answer)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewAnswerItem(answer: com.example.udlearning.data.model.UserAnswer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (answer.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (answer.isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (answer.isCorrect) "Correcto" else "Incorrecto",
                    fontWeight = FontWeight.Bold,
                    color = if (answer.isCorrect) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = "Tu respuesta:", style = MaterialTheme.typography.labelLarge)
            Text(
                text = formatAnswer(answer.userAnswer),
                style = MaterialTheme.typography.bodyMedium
            )
            
            if (!answer.isCorrect) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Respuesta correcta:", style = MaterialTheme.typography.labelLarge, color = Color(0xFF4CAF50))
                Text(
                    text = formatAnswer(answer.correctAnswer),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

fun formatAnswer(answer: Any?): String {
    return when (answer) {
        is String -> answer
        is List<*> -> answer.joinToString(", ")
        else -> answer?.toString() ?: "Sin respuesta"
    }
}
