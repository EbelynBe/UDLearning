package com.example.udlearning.ui.screens.grupos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import com.example.udlearning.data.model.Group
import com.example.udlearning.data.model.User

@Composable
fun GruposScreen(navController: NavController, viewModel: GruposViewModel = viewModel()) {
    val grupos by viewModel.grupos.collectAsState()
    val teachers by viewModel.teachers.collectAsState()

    // Estado para controlar qué grupo se está editando
    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    var showTeacherDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadGrupos()
        viewModel.loadTeachers()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFC0392B)).padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("UDLEARNING", color = Color(0xFFFFD700), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Gestionar grupos", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (grupos.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("No hay grupos registrados en la base de datos.", color = Color.White)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(grupos) { group ->
                    GroupCard(
                        group = group,
                        teachers = teachers,
                        onEditClick = {
                            selectedGroup = group
                            showTeacherDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Nombre del grupo", color = Color.White)
        OutlinedTextField(
            value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = Color.LightGray
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Programa • Semestre", color = Color.White)
        OutlinedTextField(
            value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = Color.LightGray
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
        ) {
            Text("+ Crear grupo", color = Color.Black)
        }
    }

    // Diálogo para asignar docente
    if (showTeacherDialog && selectedGroup != null) {
        AssignTeacherDialog(
            group = selectedGroup!!,
            teachers = teachers,
            onDismiss = { showTeacherDialog = false },
            onAssign = { teacherId ->
                viewModel.assignTeacherToGroup(selectedGroup!!, teacherId)
                showTeacherDialog = false
            }
        )
    }
}

@Composable
fun AssignTeacherDialog(
    group: Group,
    teachers: List<User>,
    onDismiss: () -> Unit,
    onAssign: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2C2C2C),
        title = {
            Text(
                "Asignar docente a\n${group.nombre}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            if (teachers.isEmpty()) {
                Text("No hay docentes registrados.", color = Color.LightGray)
            } else {
                Column {
                    Text(
                        "Docente actual: ${if (group.docenteId.isNullOrEmpty()) "Sin asignar" else "Asignado"}",
                        color = Color(0xFFFFD700),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Selecciona un docente:", color = Color.LightGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    teachers.forEach { teacher ->
                        val isCurrentTeacher = teacher.userId == group.docenteId
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrentTeacher) Color(0xFF1B5E20) else Color(0xFF424242)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onAssign(teacher.userId) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isCurrentTeacher) Color(0xFFFFD700) else Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        teacher.nombre,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        teacher.email,
                                        color = Color.LightGray,
                                        fontSize = 12.sp
                                    )
                                    if (isCurrentTeacher) {
                                        Text(
                                            "✓ Docente actual",
                                            color = Color(0xFFFFD700),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = Color(0xFFFFD700))
            }
        }
    )
}

@Composable
fun GroupCard(group: Group, teachers: List<User>, onEditClick: () -> Unit) {
    // Buscar el nombre del docente asignado
    val teacherName = if (!group.docenteId.isNullOrEmpty()) {
        teachers.find { it.userId == group.docenteId }?.nombre ?: "Docente asignado"
    } else {
        null
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(group.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Nivel: ${group.nivel} - Semestre: ${group.semestre}", color = Color.LightGray, fontSize = 14.sp)
                if (teacherName != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Docente: $teacherName", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Sin docente asignado", color = Color(0xFFFF8A80), fontSize = 13.sp)
                }
            }
            Row {
                IconButton(onClick = onEditClick, modifier = Modifier.background(Color(0xFF1976D2), shape = RoundedCornerShape(50))) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {}, modifier = Modifier.background(Color(0xFF7F0000), shape = RoundedCornerShape(50))) {
                    Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = Color.White)
                }
            }
        }
    }
}
