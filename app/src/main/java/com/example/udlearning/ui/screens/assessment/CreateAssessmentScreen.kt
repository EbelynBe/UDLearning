package com.example.udlearning.ui.screens.assessment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.ui.components.CustomTextField
import com.example.udlearning.ui.components.TitleHeader
import com.example.udlearning.ui.screens.session.SessionViewModel
import com.example.udlearning.ui.theme.UDLearningTheme
import com.example.udlearning.ui.theme.color.Black
import com.example.udlearning.ui.theme.color.ButtonBackground
import com.example.udlearning.ui.theme.color.DarkGrayText
import com.example.udlearning.ui.theme.color.GrayField
import com.example.udlearning.ui.theme.color.RedPrimary
import com.example.udlearning.ui.theme.color.White

@Composable
fun CreateAssessmentScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel
) {
    LaunchedEffect(Unit) {
        sessionViewModel.setIsEvaluation(true)
    }

    CreateAssessmentContent(
        title = sessionViewModel.title,
        onTitleChange = sessionViewModel::onTitleChange,
        topic = sessionViewModel.topic,
        onTopicChange = sessionViewModel::onTopicChange,
        level = sessionViewModel.level,
        onLevelChange = sessionViewModel::onLevelChange,
        learningObjective = sessionViewModel.learningObjective,
        onLearningObjectiveChange = sessionViewModel::onLearningObjectiveChange,
        duration = sessionViewModel.duration,
        onDurationChange = sessionViewModel::onDurationChange,
        errorMessage = sessionViewModel.errorMessage,
        onBackClick = { navController.popBackStack() },
        onSaveClick = {
            if (sessionViewModel.validateCreateSession()) {
                navController.navigate("schedule_session")
            }
        }
    )
}

@Composable
fun CreateAssessmentContent(
    title: String,
    onTitleChange: (String) -> Unit,
    topic: String,
    onTopicChange: (String) -> Unit,
    level: String,
    onLevelChange: (String) -> Unit,
    learningObjective: String,
    onLearningObjectiveChange: (String) -> Unit,
    duration: String,
    onDurationChange: (String) -> Unit,
    errorMessage: String?,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        TitleHeader(onBackClick = onBackClick)

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

            Text("Título de la evaluacion", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            CustomTextField(title, onTitleChange, "")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Tema", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            CustomTextField(topic, onTopicChange, "")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Nivel", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            CustomTextField(level, onLevelChange, "")

            Spacer(modifier = Modifier.height(8.dp))

            Text("Objetivo de aprendizaje", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = learningObjective,
                onValueChange = onLearningObjectiveChange,
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
                value = duration,
                onValueChange = onDurationChange,
                placeholder = "Ej: 60",
                isNumeric = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(24.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Error message if any
        errorMessage?.let { error ->
            Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }

        Button(
            onClick = onSaveClick,
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

@Preview(showBackground = true)
@Composable
fun CreateAssessmentScreenPreview() {
    UDLearningTheme {
        CreateAssessmentContent(
            title = "Matemáticas 101",
            onTitleChange = {},
            topic = "Álgebra",
            onTopicChange = {},
            level = "Intermedio",
            onLevelChange = {},
            learningObjective = "Comprender las ecuaciones de segundo grado.",
            onLearningObjectiveChange = {},
            duration = "60",
            onDurationChange = {},
            errorMessage = null,
            onBackClick = {},
            onSaveClick = {}
        )
    }
}
