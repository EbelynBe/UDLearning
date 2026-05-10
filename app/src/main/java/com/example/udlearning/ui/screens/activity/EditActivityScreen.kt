package com.example.udlearning.ui.screens.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.ui.components.TitleHeader
import com.example.udlearning.ui.theme.color.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(
    navController: NavController,
    sessionId: String,
    activityId: String,
    viewModel: EditActivityViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(activityId) {
        viewModel.loadActivity(sessionId, activityId)
    }

    if (viewModel.successMessage != null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        TitleHeader(onBackClick = { navController.popBackStack() })

        Text(
            text = "Editar ejercicio",
            color = White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "TIPO DE EJERCICIO",
            color = Color(0x99FFFFFF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Type Selector
        @Composable
        fun TypeChip(title: String, typeKey: String) {
            val isSelected = viewModel.selectedType == typeKey
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) YellowText else Color.Transparent)
                    .border(
                        1.dp,
                        if (isSelected) YellowText else Color(0x33FFFFFF),
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { viewModel.onTypeSelected(typeKey) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = if (isSelected) DarkGrayText else White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TypeChip("Quiz", "quiz")
            TypeChip("Completar", "completar")
            TypeChip("Emparej.", "emparejamiento")
            TypeChip("Traducción", "traduccion")
        }

        if (viewModel.errorMessage != null) {
            Text(
                text = viewModel.errorMessage ?: "",
                color = Color.Yellow,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (viewModel.isLoading && viewModel.enunciado.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = White)
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Common Field: Enunciado
                Text(
                    text = "Enunciado",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = viewModel.enunciado,
                    onValueChange = { viewModel.onEnunciadoChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GrayField,
                        unfocusedContainerColor = GrayField,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Escribe el enunciado aquí...") }
                )

                // Dynamic Form based on selected type
                when (viewModel.selectedType) {
                    "quiz" -> EditQuizForm(viewModel)
                    "emparejamiento" -> EditMatchingForm(viewModel)
                    "completar", "traduccion" -> EditSingleAnswerForm(viewModel)
                }

                // Common Field: Puntaje Máximo
                Text(
                    text = "Puntaje máximo",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                )
                OutlinedTextField(
                    value = viewModel.puntajeMaximo,
                    onValueChange = { viewModel.onPuntajeChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GrayField,
                        unfocusedContainerColor = GrayField,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                // Feedback / Sugerencias de mejora
                Text(
                    text = "Sugerencias de mejora (Opcional)",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Este mensaje se mostrará al estudiante si responde incorrectamente.",
                    color = Color(0x99FFFFFF),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = viewModel.feedback,
                    onValueChange = { viewModel.onFeedbackChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GrayField,
                        unfocusedContainerColor = GrayField,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Ej: Recuerda que los verbos irregulares...", color = Color(0x99FFFFFF)) },
                    minLines = 2
                )
            }
        }

        Button(
            onClick = { viewModel.updateActivity(sessionId, activityId) {} },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = RedPrimary, modifier = Modifier.size(24.dp))
            } else {
                Text("Guardar cambios", color = RedPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditQuizForm(viewModel: EditActivityViewModel) {
    Text(
        text = "Opciones de respuesta",
        color = White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    viewModel.options.forEachIndexed { index, option ->
        val prefix = ('A' + index).toString() + ")"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = prefix,
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(32.dp)
            )
            OutlinedTextField(
                value = option,
                onValueChange = { viewModel.updateOption(index, it) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GrayField,
                    unfocusedContainerColor = GrayField,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )
            if (viewModel.options.size > 2) {
                IconButton(onClick = { viewModel.removeOption(index) }) {
                    Text("X", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    TextButton(
        onClick = { viewModel.addOption() },
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text("+ Agregar opción...", color = Color(0xCCFFFFFF))
    }

    Text(
        text = "Respuesta correcta",
        color = White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        viewModel.options.forEachIndexed { index, _ ->
            val letter = ('A' + index).toString()
            val isSelected = index == viewModel.correctAnswerIndex
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) YellowText else GrayField)
                    .clickable { viewModel.selectCorrectAnswer(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter,
                    color = if (isSelected) DarkGrayText else DarkGrayText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMatchingForm(viewModel: EditActivityViewModel) {
    Text(
        text = "Pares a emparejar",
        color = White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Text("Concepto", color = Color(0x99FFFFFF), modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(16.dp))
        Text("Respuesta", color = Color(0x99FFFFFF), modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(48.dp))
    }

    viewModel.matchConcepts.indices.forEach { index ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            OutlinedTextField(
                value = viewModel.matchConcepts[index],
                onValueChange = { viewModel.updateMatchConcept(index, it) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GrayField,
                    unfocusedContainerColor = GrayField,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = viewModel.matchAnswers[index],
                onValueChange = { viewModel.updateMatchAnswer(index, it) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GrayField,
                    unfocusedContainerColor = GrayField,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )
            if (viewModel.matchConcepts.size > 2) {
                IconButton(onClick = { viewModel.removeMatchPair(index) }, modifier = Modifier.width(48.dp)) {
                    Text("X", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }

    TextButton(
        onClick = { viewModel.addMatchPair() },
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text("+ Agregar par...", color = Color(0xCCFFFFFF))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSingleAnswerForm(viewModel: EditActivityViewModel) {
    Text(
        text = "Respuesta esperada",
        color = White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    OutlinedTextField(
        value = viewModel.expectedAnswer,
        onValueChange = { viewModel.onExpectedAnswerChange(it) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GrayField,
            unfocusedContainerColor = GrayField,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        placeholder = { Text("Escribe la respuesta correcta...") }
    )
}
