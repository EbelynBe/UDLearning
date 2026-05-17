package com.example.udlearning.ui.screens.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.ui.components.CustomTextField
import com.example.udlearning.ui.components.TitleHeader
import com.example.udlearning.ui.theme.color.*

@Composable
fun CreateSessionScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel
) {
    LaunchedEffect(Unit) {
        sessionViewModel.setIsEvaluation(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        TitleHeader(onBackClick = { navController.popBackStack() })

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Crear sesión",
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Text("Título de la sesión", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            CustomTextField(sessionViewModel.title, sessionViewModel::onTitleChange, "")
            
            Spacer(modifier = Modifier.height(8.dp))

            Text("Tema", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            CustomTextField(sessionViewModel.topic, sessionViewModel::onTopicChange, "")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Nivel", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            CustomTextField(sessionViewModel.level, sessionViewModel::onLevelChange, "")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Objetivo de aprendizaje", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = sessionViewModel.learningObjective,
                onValueChange = sessionViewModel::onLearningObjectiveChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GrayField,
                    unfocusedContainerColor = GrayField,
                    focusedIndicatorColor = Black,
                    unfocusedIndicatorColor = Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Tiempo máximo (minutos)", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            CustomTextField(
                value = sessionViewModel.duration,
                onValueChange = sessionViewModel::onDurationChange,
                placeholder = "Ej: 60",
                isNumeric = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Error message if any
        sessionViewModel.errorMessage?.let { error ->
            Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }

        Button(
            onClick = { 
                if (sessionViewModel.validateCreateSession()) {
                    navController.navigate("schedule_session") 
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonBackground)
        ) {
            Text("Guardar sesión", color = DarkGrayText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
