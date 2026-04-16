package com.example.udlearning.ui.screens.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.data.model.Session
import com.example.udlearning.ui.components.TitleHeader
import com.example.udlearning.ui.theme.color.*

@Composable
fun ManageSessionScreen(
    navController: NavController,
    sessionId: String,
    viewModel: ManageSessionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(sessionId) {
        viewModel.loadSession(sessionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        TitleHeader(onBackClick = { navController.popBackStack() })

        Text(
            text = "Gestionar sesión",
            color = White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        if (viewModel.isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = White)
            }
        } else if (viewModel.errorMessage != null) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(viewModel.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            }
        } else if (viewModel.session != null) {
            val session = viewModel.session!!

            // Resumen de la sesión
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFC62828))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = session.titulo,
                        color = White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${session.nivel} · ${session.tema}",
                        color = Color(0xCCFFFFFF),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(GrayTag)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = session.estado.replaceFirstChar { it.uppercase() },
                            color = White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = "OPCIONES DE GESTIÓN",
                color = Color(0x99FFFFFF),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Editar
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    navController.navigate("edit_session/${session.sessionId}")
                }.padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFC62828))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(OrangeTag),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✎", color = White, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Editar sesión", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Modifica título, fechas y grupos", color = Color(0xCCFFFFFF), fontSize = 12.sp)
                    }
                }
            }

            // Archivar
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    viewModel.showArchiveDialog = true
                }.padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFC62828))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF1976D2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("▫️", color = White, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Archivar sesión", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Conserva el historial de la sesión", color = Color(0xCCFFFFFF), fontSize = 12.sp)
                    }
                }
            }

            // Eliminar
            Card(
                modifier = Modifier.fillMaxWidth().clickable {
                    viewModel.prepareDelete()
                },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFC62828))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF8B0000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("×", color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Eliminar sesión", color = White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Esta acción no se puede deshacer", color = Color(0xCCFFFFFF), fontSize = 12.sp)
                    }
                }
            }
        }

        // Confirmation Dialogs
        if (viewModel.showArchiveDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showArchiveDialog = false },
                title = { Text("Archivar Sesión", fontWeight = FontWeight.Bold) },
                text = { Text("¿Estás seguro de que deseas archivar esta sesión? Dejará de aparecer en la lista de pendientes pero se conservará el historial.") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.showArchiveDialog = false
                        viewModel.archiveSession { navController.popBackStack() }
                    }) {
                        Text("Confirmar", color = RedPrimary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showArchiveDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }

        if (viewModel.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showDeleteDialog = false },
                title = { Text("Eliminar Sesión", color = Color.Red, fontWeight = FontWeight.Bold) },
                text = { 
                    Column {
                        Text("¿Estás seguro de que deseas eliminar esta sesión permanentemente?")
                        if (viewModel.participantCount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "ADVERTENCIA: Esta sesión ya cuenta con ${viewModel.participantCount} estudiantes registrados. Eliminarla borrará todo su progreso.",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.showDeleteDialog = false
                        viewModel.deleteSession { navController.popBackStack() }
                    }) {
                        Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showDeleteDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Se pedirá confirmación antes de ejecutar\ncualquier acción",
            color = Color(0x99FFFFFF),
            fontSize = 12.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
        ) {
            Text("Cancelar", color = White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
