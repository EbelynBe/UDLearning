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
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ConfiguracionScreen(navController: NavController, viewModel: ConfiguracionViewModel = viewModel()) {
    val horarioLimite by viewModel.horarioLimite.collectAsState()
    val tiempoLimite by viewModel.tiempoLimite.collectAsState()
    val alertasActivas by viewModel.alertasActivas.collectAsState()
    val permisosRol by viewModel.permisosRol.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val configLoaded by viewModel.configLoaded.collectAsState()
    val horarioError by viewModel.horarioError.collectAsState()
    val tiempoError by viewModel.tiempoError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadConfiguracion() }
    LaunchedEffect(mensaje) { mensaje?.let { snackbarHostState.showSnackbar(it); viewModel.clearMensaje() } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) { data -> Snackbar(snackbarData = data, containerColor = Color(0xFF2C2C2C), contentColor = Color.White) } },
        containerColor = Color.Transparent
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFC0392B)).padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }
                Text("UDLEARNING", color = Color(0xFFFFD700), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Configuración", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            if (!configLoaded && isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFFD700))
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                // Status badge
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)), modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("● Configuración cargada desde servidor", color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("HORARIOS Y TIEMPOS", color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // Horario límite
                ConfigCard {
                    Text("Horario límite de actividades", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Hora máxima para entregar (formato HH:MM)", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Valor actual: $horarioLimite", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    ConfigTextField(value = horarioLimite, onValueChange = { viewModel.onHorarioChange(it) }, isError = horarioError != null, placeholder = "Ej: 23:59")
                    if (horarioError != null) {
                        Text(horarioError!!, color = Color(0xFFFF8A80), fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tiempo límite
                ConfigCard {
                    Text("Tiempo límite por evaluación", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Duración máxima en minutos (1-180)", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Valor actual: $tiempoLimite min", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    ConfigTextField(value = tiempoLimite, onValueChange = { viewModel.onTiempoChange(it) }, isError = tiempoError != null, placeholder = "Ej: 60")
                    if (tiempoError != null) {
                        Text(tiempoError!!, color = Color(0xFFFF8A80), fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("ALERTAS Y PERMISOS", color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // Alertas toggle
                ConfigCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Alertas automáticas", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Nuevas sesiones y evaluaciones", color = Color.LightGray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (alertasActivas) "Estado: Activadas ✓" else "Estado: Desactivadas ✗", color = if (alertasActivas) Color(0xFF4CAF50) else Color(0xFFFF8A80), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Switch(checked = alertasActivas, onCheckedChange = { viewModel.onAlertasChange(it) }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF2E7D32), uncheckedThumbColor = Color.LightGray, uncheckedTrackColor = Color.Gray))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Permisos por rol
                ConfigCard {
                    Text("Permisos por rol", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Estudiante - Docente - Administrador", color = Color.LightGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Valor actual: $permisosRol", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    ConfigTextField(value = permisosRol, onValueChange = { viewModel.onPermisosChange(it) }, placeholder = "Ej: estudiante,docente,administrador")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(onClick = { viewModel.saveConfiguracion() }, enabled = !isLoading, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)), shape = RoundedCornerShape(24.dp)) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                    else Text("Guardar configuración", color = Color(0xFF2C3E50), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun ConfigCard(content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent), border = BorderStroke(1.dp, Color.White), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

@Composable
fun ConfigTextField(value: String, onValueChange: (String) -> Unit, isError: Boolean = false, placeholder: String = "") {
    OutlinedTextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(24.dp), isError = isError, placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.LightGray, focusedContainerColor = Color.LightGray, unfocusedBorderColor = if (isError) Color(0xFFFF8A80) else Color.Transparent, focusedBorderColor = if (isError) Color(0xFFFF8A80) else Color.Transparent, errorBorderColor = Color(0xFFFF8A80), unfocusedTextColor = Color.Black, focusedTextColor = Color.Black))
}
