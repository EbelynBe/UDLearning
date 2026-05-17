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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.data.model.Activity
import com.example.udlearning.ui.components.TitleHeader
import com.example.udlearning.ui.theme.color.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.LayoutCoordinates

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolveSessionScreen(
    navController: NavController,
    sessionId: String,
    viewModel: SolveSessionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(sessionId) {
        viewModel.loadActivities(sessionId)
    }

    var showFeedback by remember { mutableStateOf(false) }
    var lastAnswerIsValid by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        TitleHeader(onBackClick = { navController.popBackStack() })

        when {
            viewModel.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = White)
                }
            }
            viewModel.errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(viewModel.errorMessage ?: "", color = White, textAlign = TextAlign.Center)
                }
            }
            viewModel.isFinished -> {
                FinalScoreScreen(viewModel.score.toInt(), viewModel.totalPossibleScore.toInt()) {
                    navController.popBackStack()
                }
            }
            else -> {
                val currentActivity = viewModel.activities.getOrNull(viewModel.currentIndex)
                if (currentActivity != null) {
                    // Progress Bar
                    val progress = (viewModel.currentIndex + 1).toFloat() / viewModel.activities.size.toFloat()
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = YellowText,
                        trackColor = GrayField
                    )
                    Text(
                        text = "Actividad ${viewModel.currentIndex + 1} de ${viewModel.activities.size}",
                        color = Color(0x99FFFFFF),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                    )

                    var isValid by remember(currentActivity.activityId) { mutableStateOf(false) }
                    var hasSelected by remember(currentActivity.activityId) { mutableStateOf(false) }
                    var lastUserAnswerRaw by remember(currentActivity.activityId) { mutableStateOf<Any?>(null) }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        if (currentActivity.tipo != "completar") {
                            Text(
                                text = currentActivity.descripcion,
                                color = White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )
                        }

                        when (currentActivity.tipo) {
                            "quiz" -> SolveQuizForm(currentActivity) { valid, selected, raw -> 
                                isValid = valid
                                hasSelected = selected 
                                lastUserAnswerRaw = raw
                            }
                            "completar", "traduccion" -> SolveStringForm(currentActivity, viewModel) { valid, selected, raw -> 
                                isValid = valid
                                hasSelected = selected
                                lastUserAnswerRaw = raw
                            }
                            "emparejamiento" -> SolveMatchingForm(currentActivity) { valid, selected, raw -> 
                                isValid = valid
                                hasSelected = selected
                                lastUserAnswerRaw = raw
                            }
                            else -> Text("Tipo no soportado: ${currentActivity.tipo}", color = White)
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                lastAnswerIsValid = isValid
                                showFeedback = true
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (hasSelected) ButtonBackground else Color(0x33FFFFFF),
                                contentColor = if (hasSelected) DarkGrayText else Color(0x66FFFFFF)
                            ),
                            enabled = hasSelected
                        ) {
                            Text(
                                text = "Verificar",
                                color = DarkGrayText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (showFeedback) {
                        FeedbackBottomSheet(
                            sheetState = sheetState,
                            isCorrect = lastAnswerIsValid,
                            correctAnswerText = viewModel.getCorrectAnswerText(currentActivity),
                            feedbackText = currentActivity.contenido["feedback"] as? String ?: "",
                            isLastActivity = viewModel.currentIndex == viewModel.activities.size - 1,
                            onContinue = {
                                showFeedback = false
                                viewModel.moveToNext(lastAnswerIsValid, lastUserAnswerRaw)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FinalScoreScreen(score: Int, total: Int, onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("¡Has terminado!", color = White, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        Text("Tu puntaje final es:", color = Color(0x99FFFFFF), fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
        Text("$score / $total", color = YellowText, fontSize = 48.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(bottom = 32.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonBackground)
        ) {
            Text("Volver al inicio", color = DarkGrayText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackBottomSheet(
    sheetState: SheetState,
    isCorrect: Boolean,
    correctAnswerText: String,
    feedbackText: String,
    isLastActivity: Boolean,
    onContinue: () -> Unit
) {
    val bgColor = if (isCorrect) Color(0xFF1B5E20) else Color(0xFF7F0000)
    val accentColor = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFEF5350)

    ModalBottomSheet(
        onDismissRequest = { /* No cerrar al tocar fuera */ },
        sheetState = sheetState,
        containerColor = bgColor,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isCorrect) "✓" else "✗",
                color = accentColor,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = if (isCorrect) "¡Correcto!" else "Incorrecto",
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            if (!isCorrect && correctAnswerText.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33FFFFFF))
                        .padding(16.dp)
                ) {
                    Text("Respuesta correcta:", color = Color(0xCCFFFFFF), fontSize = 13.sp)
                    Text(correctAnswerText, color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (!isCorrect && feedbackText.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22FFFFFF))
                        .padding(16.dp)
                ) {
                    Text("💡 Sugerencia del docente:", color = YellowText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(feedbackText, color = Color(0xEEFFFFFF), fontSize = 15.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Text(
                    text = if (isLastActivity) "Ver resultados" else "Continuar",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SolveQuizForm(activity: Activity, onValidityChange: (Boolean, Boolean, Any?) -> Unit) {
    val options = activity.contenido["opciones"] as? List<String> ?: emptyList()
    val correctAnswer = activity.contenido["respuesta_correcta"] as? String ?: ""
    var selectedOption by remember(activity.activityId) { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedOption, activity.activityId) {
        val isValid = selectedOption?.trim().equals(correctAnswer.trim(), ignoreCase = true)
        val hasSelected = selectedOption != null
        onValidityChange(isValid, hasSelected, selectedOption)
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            val isSelected = selectedOption == option
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) YellowText else GrayField)
                    .clickable { selectedOption = option }
                    .padding(16.dp)
            ) {
                Text(
                    text = option,
                    color = DarkGrayText,
                    fontSize = 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun SolveStringForm(activity: Activity, viewModel: SolveSessionViewModel, onValidityChange: (Boolean, Boolean, Any?) -> Unit) {
    val expectedAnswer = activity.contenido["respuesta_esperada"] as? String ?: ""
    val pregunta = activity.contenido["pregunta"] as? String ?: ""
    var input by remember(activity.activityId) { mutableStateOf("") }

    LaunchedEffect(input, activity.activityId) {
        val isValid = viewModel.verifyStringAnswer(input, expectedAnswer)
        // Validation: Empty space is always incorrect/invalid
        val hasSelected = input.isNotBlank()
        onValidityChange(isValid && hasSelected, hasSelected, input)
    }

    if (activity.tipo == "completar" && pregunta.contains("__________")) {
        val parts = pregunta.split("__________")
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                if (parts[0].isNotBlank()) {
                    Text(parts[0], color = White, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                
                // Inline TextField for the blank
                OutlinedTextField(
                    value = input,
                    onValueChange = { if (it.length <= expectedAnswer.length + 5) input = it },
                    modifier = Modifier.widthIn(min = 100.dp, max = 200.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GrayField,
                        unfocusedContainerColor = GrayField,
                        focusedTextColor = DarkGrayText,
                        unfocusedTextColor = DarkGrayText,
                        focusedIndicatorColor = YellowText
                    ),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )

                if (parts.size > 1 && parts[1].isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(parts[1], color = White, fontSize = 18.sp)
                }
            }
        }
    } else {
        // Fallback or Translation type
        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = GrayField,
                unfocusedContainerColor = GrayField,
                focusedTextColor = DarkGrayText,
                unfocusedTextColor = DarkGrayText
            ),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("Escribe tu respuesta aquí...", color = Color(0x66000000)) }
        )
    }
}

@Composable
fun SolveMatchingForm(activity: Activity, onValidityChange: (Boolean, Boolean, Any?) -> Unit) {
    val pares = activity.contenido["pares"] as? List<Map<String, String>> ?: emptyList()
    val concepts = remember(activity.activityId) { pares.map { it["concepto"] ?: "" } }
    val answers = remember(activity.activityId) { pares.map { it["respuesta"] ?: "" }.shuffled() }

    var selectedConcept by remember(activity.activityId) { mutableStateOf<String?>(null) }
    var selectedAnswer by remember(activity.activityId) { mutableStateOf<String?>(null) }
    val matches = remember(activity.activityId) { mutableStateMapOf<String, String>() }

    // Drag and Drop state
    var draggedConcept by remember { mutableStateOf<String?>(null) }
    var dragOffset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    val dropZones = remember { mutableStateMapOf<String, androidx.compose.ui.layout.LayoutCoordinates>() }

    LaunchedEffect(matches.size, activity.activityId) {
        var isAllCorrect = matches.size == pares.size
        if (isAllCorrect) {
            pares.forEach { par ->
                val concept = par["concepto"] ?: ""
                val correctAns = par["respuesta"] ?: ""
                val currentMatch = matches[concept] ?: ""
                if (!currentMatch.trim().equals(correctAns.trim(), ignoreCase = true)) {
                    isAllCorrect = false
                }
            }
        }
        val hasSelected = matches.isNotEmpty()
        onValidityChange(isAllCorrect, hasSelected, matches.toMap())
    }

    LaunchedEffect(selectedConcept, selectedAnswer, activity.activityId) {
        if (selectedConcept != null && selectedAnswer != null) {
            matches[selectedConcept!!] = selectedAnswer!!
            selectedConcept = null
            selectedAnswer = null
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            // Column 1: Concepts
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                concepts.forEach { concept ->
                    val isMatched = matches.containsKey(concept)
                    val isSelected = selectedConcept == concept
                    val isBeingDragged = draggedConcept == concept
                    
                    val bgColor = if (isMatched) Color(0xFF4CAF50) else if (isSelected) YellowText else GrayField
                    
                    var cardCoordinates: LayoutCoordinates? by remember { mutableStateOf(null) }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isBeingDragged) bgColor.copy(alpha = 0.5f) else bgColor)
                            .onGloballyPositioned { cardCoordinates = it }
                            .then(
                                if (!isMatched) {
                                    Modifier.pointerInput(concept) {
                                        detectDragGestures(
                                            onDragStart = { draggedConcept = concept },
                                            onDragEnd = {
                                                draggedConcept = null
                                                dragOffset = androidx.compose.ui.geometry.Offset.Zero
                                            },
                                            onDragCancel = { 
                                                draggedConcept = null
                                                dragOffset = androidx.compose.ui.geometry.Offset.Zero
                                            },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                dragOffset += dragAmount
                                                
                                                val currentCoord = cardCoordinates
                                                if (currentCoord != null) {
                                                    val globalPointer = change.position + currentCoord.positionInWindow()
                                                    dropZones.forEach { (ans, coords) ->
                                                        if (coords.isAttached && coords.boundsInWindow().contains(globalPointer)) {
                                                            matches[concept] = ans
                                                            draggedConcept = null
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    }
                                } else Modifier
                            )
                            .clickable { if (!isMatched) selectedConcept = concept else matches.remove(concept) }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(concept, color = if (bgColor == GrayField) White else DarkGrayText, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Column 2: Answers (Drop Zones)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                answers.forEach { answer ->
                    val isMatched = matches.containsValue(answer)
                    val isSelected = selectedAnswer == answer
                    val bgColor = if (isMatched) Color(0xFF4CAF50) else if (isSelected) YellowText else GrayField
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor)
                            .onGloballyPositioned { dropZones[answer] = it }
                            .clickable { if (!isMatched) selectedAnswer = answer }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(answer, color = if (bgColor == GrayField) White else DarkGrayText, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        // Drag overlay
        draggedConcept?.let { concept ->
            Box(
                modifier = Modifier
                    .offset { androidx.compose.ui.unit.IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt()) }
                    .width(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(YellowText.copy(alpha = 0.9f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(concept, color = DarkGrayText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
