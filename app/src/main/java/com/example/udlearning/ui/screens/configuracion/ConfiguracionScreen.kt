package com.example.udlearning.ui.screens.configuracion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun ConfiguracionScreen(navController: NavController) {
    var horarioLimite by remember { mutableStateOf("") }
    var tiempoLimite by remember { mutableStateOf("") }
    var alertasActivas by remember { mutableStateOf(true) }
    var permisosRol by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0392B))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("UDLEARNING", color = Color(0xFFFFD700), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Configuración", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("HORARIOS Y TIEMPOS", color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        ConfigCard {
            Text("Horario límite de actividades", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Hora máxima para entregar: 11:59 PM", color = Color.LightGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            ConfigTextField(value = horarioLimite, onValueChange = { horarioLimite = it })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ConfigCard {
            Text("Tiempo límite por evaluación", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Duración máxima: 60 min", color = Color.LightGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            ConfigTextField(value = tiempoLimite, onValueChange = { tiempoLimite = it })
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("ALERTAS Y PERMISOS", color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        ConfigCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Alertas automáticas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Nuevas sesiones y evaluaciones", color = Color.LightGray, fontSize = 12.sp)
                }
                Switch(
                    checked = alertasActivas,
                    onCheckedChange = { alertasActivas = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF2E7D32),
                        uncheckedThumbColor = Color.LightGray,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ConfigCard {
            Text("Permisos por rol", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Estudiante - Docente - Administrador", color = Color.LightGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            ConfigTextField(value = permisosRol, onValueChange = { permisosRol = it })
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { /* Guardar */ },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Guardar configuración", color = Color(0xFF2C3E50), fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun ConfigCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun ConfigTextField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.LightGray,
            focusedContainerColor = Color.LightGray,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black
        )
    )
}
