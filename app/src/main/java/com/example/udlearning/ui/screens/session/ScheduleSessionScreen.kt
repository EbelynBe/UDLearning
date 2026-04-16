package com.example.udlearning.ui.screens.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.ui.components.CustomTextField
import com.example.udlearning.ui.components.TitleHeader
import com.example.udlearning.ui.theme.color.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSessionScreen(
    navController: NavController,
    sessionViewModel: SessionViewModel
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
                text = "Programar sesión",
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "SELECCIONAR GRUPOS",
                color = Color(0x99FFFFFF), // Semi-transparent white
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            sessionViewModel.availableGroups.forEach { group ->
                GroupCheckbox(
                    text = "${group.nombre} – ${group.nivel} – Semestre ${group.semestre}",
                    checked = sessionViewModel.selectedGroupIds.contains(group.groupId),
                    onCheckedChange = { isChecked ->
                        sessionViewModel.toggleGroupSelection(group.groupId, isChecked)
                    }
                )
            }

            HorizontalDivider(color = Color(0x4DFFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))

            DatePickerRow(
                label = "Fecha de inicio",
                dateValue = sessionViewModel.startDate,
                onDateChange = sessionViewModel::onStartDateChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            DatePickerRow(
                label = "Fecha de fin",
                dateValue = sessionViewModel.endDate,
                onDateChange = sessionViewModel::onEndDateChange
            )
            
            HorizontalDivider(color = Color(0x4DFFFFFF), thickness = 1.dp, modifier = Modifier.padding(vertical = 16.dp))
            
            Text(
                text = "La sesión se notificará automáticamente a los\nestudiantes seleccionados",
                color = Color(0x99FFFFFF),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Error message if any
        sessionViewModel.errorMessage?.let { error ->
            Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp))
        }

        Button(
            onClick = { 
                sessionViewModel.saveSessionWithSchedule {
                    Toast.makeText(context, "Creación de sesión exitosa", Toast.LENGTH_SHORT).show()
                    sessionViewModel.clearState()
                    navController.navigate("home") { popUpTo("home") { inclusive = true } } 
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonBackground),
            enabled = !sessionViewModel.isLoading
        ) {
            if (sessionViewModel.isLoading) {
                CircularProgressIndicator(color = DarkGrayText, modifier = Modifier.size(24.dp))
            } else {
                Text("Confirmar", color = DarkGrayText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun GroupCheckbox(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(if (checked) YellowText else GrayField, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                // A simple checkmark representation
                Text("✓", color = DarkGrayText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, color = White, fontSize = 14.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerRow(
    label: String,
    dateValue: String,
    onDateChange: (String) -> Unit,
    initialDateMillis: Long? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    val dateMillis = datePickerState.selectedDateMillis
                    if (dateMillis != null) {
                        // Adjusting for timezone offset so the date matches local selection
                        val offset = java.util.TimeZone.getDefault().getOffset(dateMillis).toLong()
                        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                        onDateChange(formatter.format(java.util.Date(dateMillis - offset)))
                    }
                }) {
                    Text("OK", color = RedPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = DarkGrayText)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Text(label, color = White, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White, RoundedCornerShape(20.dp))
            .clickable { showDialog = true }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = if (dateValue.isEmpty()) "Seleccionar fecha" else dateValue,
            color = if (dateValue.isEmpty()) Color.Gray else DarkGrayText
        )
    }
}
