package com.example.udlearning.ui.screens.session

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.ui.components.CustomTextField
import com.example.udlearning.ui.components.TitleHeader
import com.example.udlearning.ui.theme.color.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSessionScreen(
    navController: NavController,
    viewModel: ManageSessionViewModel
) {
    val context = LocalContext.current

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
                text = "Editar sesión",
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // ── Campos básicos ──────────────────────────────────────────
            Text("Título de la sesión", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            CustomTextField(viewModel.title, viewModel::onTitleChange, "")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Tema", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            CustomTextField(viewModel.topic, viewModel::onTopicChange, "")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Nivel", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            CustomTextField(viewModel.level, viewModel::onLevelChange, "")
            Spacer(modifier = Modifier.height(8.dp))

            Text("Objetivo de aprendizaje", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = viewModel.learningObjective,
                onValueChange = viewModel::onLearningObjectiveChange,
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(20.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GrayField,
                    unfocusedContainerColor = GrayField,
                    focusedIndicatorColor = Black,
                    unfocusedIndicatorColor = Black
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Tiempo máximo (minutos)", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            CustomTextField(
                value = viewModel.duration,
                onValueChange = viewModel::onDurationChange,
                placeholder = "Ej: 60",
                isNumeric = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // ── Estado ──────────────────────────────────────────────────
            Text("Estado de la sesión", color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
            EstadoDropdown(
                selected = viewModel.estado,
                onSelect = viewModel::onEstadoChange
            )

            HorizontalDivider(color = Color(0x4DFFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

            // ── Grupos ──────────────────────────────────────────────────
            Text(
                text = "SELECCIONAR GRUPOS",
                color = Color(0x99FFFFFF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (viewModel.availableGroups.isEmpty()) {
                Text("Cargando grupos...", color = Color(0x99FFFFFF), fontSize = 13.sp)
            } else {
                viewModel.availableGroups.forEach { group ->
                    GroupCheckbox(
                        text = "${group.nombre} – ${group.nivel} – Semestre ${group.semestre}",
                        checked = viewModel.selectedGroupIds.contains(group.groupId),
                        onCheckedChange = { isChecked ->
                            viewModel.toggleGroupSelection(group.groupId, isChecked)
                        }
                    )
                }
            }

            HorizontalDivider(color = Color(0x4DFFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

            // ── Fechas ──────────────────────────────────────────────────
            DatePickerRow(
                label = "Fecha de inicio",
                dateValue = viewModel.startDate,
                onDateChange = viewModel::onStartDateChange,
                initialDateMillis = viewModel.startDateMillis()
            )
            Spacer(modifier = Modifier.height(16.dp))

            DatePickerRow(
                label = "Fecha de fin",
                dateValue = viewModel.endDate,
                onDateChange = viewModel::onEndDateChange,
                initialDateMillis = viewModel.endDateMillis()
            )

            HorizontalDivider(color = Color(0x4DFFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

            Text(
                text = "Los cambios se notificarán automáticamente\na los estudiantes asignados",
                color = Color(0x99FFFFFF),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("session_activities/${viewModel.session?.sessionId}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
            ) {
                Text("Ver / Editar actividades", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // ── Error ────────────────────────────────────────────────────
        viewModel.errorMessage?.let { error ->
            Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }

        // ── Guardar ──────────────────────────────────────────────────
        Button(
            onClick = {
                viewModel.updateSession {
                    Toast.makeText(context, "Sesión actualizada correctamente", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonBackground),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = DarkGrayText, modifier = Modifier.size(24.dp))
            } else {
                Text("Guardar cambios", color = DarkGrayText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadoDropdown(selected: String, onSelect: (String) -> Unit) {
    val options = listOf("pendiente", "activa", "archivada", "finalizada")
    val labels = mapOf(
        "pendiente" to "Pendiente",
        "activa" to "Activa",
        "archivada" to "Archivada",
        "finalizada" to "Finalizada"
    )
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = labels[selected] ?: selected.replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.ui.graphics.Color.White,
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.White,
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                focusedTextColor = com.example.udlearning.ui.theme.color.DarkGrayText,
                unfocusedTextColor = com.example.udlearning.ui.theme.color.DarkGrayText
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(labels[option] ?: option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
