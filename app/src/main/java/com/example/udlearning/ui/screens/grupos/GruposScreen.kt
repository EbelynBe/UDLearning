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
    val students by viewModel.students.collectAsState()
    val createNombre by viewModel.createNombre.collectAsState()
    val createNivel by viewModel.createNivel.collectAsState()
    val createSemestre by viewModel.createSemestre.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    var showTeacherDialog by remember { mutableStateOf(false) }
    var showStudentDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var groupToDelete by remember { mutableStateOf<Group?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { viewModel.loadGrupos(); viewModel.loadTeachers(); viewModel.loadStudents() }
    LaunchedEffect(mensaje) { mensaje?.let { snackbarHostState.showSnackbar(it); viewModel.clearMensaje() } }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) { data -> Snackbar(snackbarData = data, containerColor = Color(0xFF2C2C2C), contentColor = Color.White) } },
        containerColor = Color.Transparent
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFC0392B)).padding(padding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White) }
                Text("UDLEARNING", color = Color(0xFFFFD700), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Gestionar grupos", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (grupos.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) { Text("No hay grupos registrados.", color = Color.White) }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(grupos) { group ->
                        GroupCard(group = group, teachers = teachers,
                            onEditClick = { selectedGroup = group; showEditDialog = true },
                            onDeleteClick = { groupToDelete = group },
                            onAssignTeacher = { selectedGroup = group; showTeacherDialog = true },
                            onAssignStudents = { selectedGroup = group; showStudentDialog = true }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Nombre del grupo", color = Color.White)
            OutlinedTextField(value = createNombre, onValueChange = { viewModel.onNombreChange(it) }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Ej: Grupo A1", color = Color.Gray) }, colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.LightGray, focusedContainerColor = Color.LightGray, unfocusedTextColor = Color.Black, focusedTextColor = Color.Black))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nivel", color = Color.White)
            OutlinedTextField(value = createNivel, onValueChange = { viewModel.onNivelChange(it) }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Ej: Básico, Intermedio", color = Color.Gray) }, colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.LightGray, focusedContainerColor = Color.LightGray, unfocusedTextColor = Color.Black, focusedTextColor = Color.Black))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Semestre", color = Color.White)
            OutlinedTextField(value = createSemestre, onValueChange = { viewModel.onSemestreChange(it) }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Ej: 2026-1", color = Color.Gray) }, colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.LightGray, focusedContainerColor = Color.LightGray, unfocusedTextColor = Color.Black, focusedTextColor = Color.Black))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.createGroup() }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)), shape = RoundedCornerShape(24.dp)) {
                Text("+ Crear grupo", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Assign teacher dialog
    if (showTeacherDialog && selectedGroup != null) {
        AssignTeacherDialog(group = selectedGroup!!, teachers = teachers, onDismiss = { showTeacherDialog = false }, onAssign = { teacherId -> viewModel.assignTeacherToGroup(selectedGroup!!, teacherId); showTeacherDialog = false })
    }

    // Assign students dialog
    if (showStudentDialog && selectedGroup != null) {
        AssignStudentsDialog(group = selectedGroup!!, students = students, onDismiss = { showStudentDialog = false }, onToggleStudent = { student -> viewModel.assignStudentToGroup(student, selectedGroup!!.groupId) })
    }

    // Edit group dialog
    if (showEditDialog && selectedGroup != null) {
        EditGroupDialog(group = selectedGroup!!, onDismiss = { showEditDialog = false }, onConfirm = { nombre, semestre -> viewModel.updateGroup(selectedGroup!!, nombre, semestre); showEditDialog = false })
    }

    // Delete confirmation dialog
    if (groupToDelete != null) {
        AlertDialog(onDismissRequest = { groupToDelete = null }, containerColor = Color(0xFF2C2C2C),
            title = { Text("Eliminar grupo", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar el grupo \"${groupToDelete!!.nombre}\"? Esta acción no se puede deshacer.", color = Color.LightGray) },
            confirmButton = { Button(onClick = { viewModel.deleteGroup(groupToDelete!!.groupId); groupToDelete = null }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))) { Text("Eliminar", color = Color.White) } },
            dismissButton = { TextButton(onClick = { groupToDelete = null }) { Text("Cancelar", color = Color.LightGray) } }
        )
    }
}

@Composable
fun EditGroupDialog(group: Group, onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var editNombre by remember { mutableStateOf(group.nombre) }
    var editSemestre by remember { mutableStateOf(group.semestre) }

    AlertDialog(onDismissRequest = onDismiss, containerColor = Color(0xFF2C2C2C),
        title = { Text("Editar grupo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column {
                Text("Nombre:", color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(value = editNombre, onValueChange = { editNombre = it }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFF424242), focusedContainerColor = Color(0xFF424242), unfocusedTextColor = Color.White, focusedTextColor = Color.White))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Semestre:", color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(value = editSemestre, onValueChange = { editSemestre = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Ej: 2026-1", color = Color.Gray) }, colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFF424242), focusedContainerColor = Color(0xFF424242), unfocusedTextColor = Color.White, focusedTextColor = Color.White))
            }
        },
        confirmButton = { Button(onClick = { onConfirm(editNombre, editSemestre) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))) { Text("Guardar", color = Color.Black, fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.LightGray) } }
    )
}

@Composable
fun AssignTeacherDialog(group: Group, teachers: List<User>, onDismiss: () -> Unit, onAssign: (String) -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = Color(0xFF2C2C2C),
        title = { Text("Asignar docente a\n${group.nombre}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            if (teachers.isEmpty()) { Text("No hay docentes registrados.", color = Color.LightGray) }
            else {
                Column {
                    Text("Docente actual: ${if (group.docenteId.isNullOrEmpty()) "Sin asignar" else "Asignado"}", color = Color(0xFFFFD700), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Selecciona un docente:", color = Color.LightGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    teachers.forEach { teacher ->
                        val isCurrentTeacher = teacher.userId == group.docenteId
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (isCurrentTeacher) Color(0xFF1B5E20) else Color(0xFF424242)), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onAssign(teacher.userId) }) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = if (isCurrentTeacher) Color(0xFFFFD700) else Color.White, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(teacher.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(teacher.email, color = Color.LightGray, fontSize = 12.sp)
                                    if (isCurrentTeacher) { Text("✓ Docente actual", color = Color(0xFFFFD700), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar", color = Color(0xFFFFD700)) } }
    )
}

@Composable
fun AssignStudentsDialog(group: Group, students: List<User>, onDismiss: () -> Unit, onToggleStudent: (User) -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = Color(0xFF2C2C2C),
        title = { Text("Asignar estudiantes a\n${group.nombre}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            if (students.isEmpty()) { Text("No hay estudiantes registrados.", color = Color.LightGray) }
            else {
                Column {
                    Text("${group.estudianteIds.size} estudiantes asignados", color = Color(0xFFFFD700), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    students.forEach { student ->
                        val isAssigned = student.userId in group.estudianteIds || student.groupId == group.groupId
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = if (isAssigned) Color(0xFF1B5E20) else Color(0xFF424242)), modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onToggleStudent(student) }) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = if (isAssigned) Color(0xFFFFD700) else Color.White, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(student.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(student.email, color = Color.LightGray, fontSize = 11.sp)
                                }
                                if (isAssigned) { Text("✓", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar", color = Color(0xFFFFD700)) } }
    )
}

@Composable
fun GroupCard(group: Group, teachers: List<User>, onEditClick: () -> Unit, onDeleteClick: () -> Unit, onAssignTeacher: () -> Unit, onAssignStudents: () -> Unit) {
    val teacherName = if (!group.docenteId.isNullOrEmpty()) { teachers.find { it.userId == group.docenteId }?.nombre ?: "Docente asignado" } else { null }

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.Transparent), border = androidx.compose.foundation.BorderStroke(1.dp, Color.White), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(group.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Nivel: ${group.nivel} - Semestre: ${group.semestre}", color = Color.LightGray, fontSize = 14.sp)
                    if (teacherName != null) { Spacer(modifier = Modifier.height(4.dp)); Text("Docente: $teacherName", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                    else { Spacer(modifier = Modifier.height(4.dp)); Text("Sin docente asignado", color = Color(0xFFFF8A80), fontSize = 13.sp) }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Estudiantes: ${group.estudianteIds.size}", color = Color.LightGray, fontSize = 12.sp)
                }
                Row {
                    IconButton(onClick = onEditClick, modifier = Modifier.background(Color(0xFF1976D2), shape = RoundedCornerShape(50))) { Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White) }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDeleteClick, modifier = Modifier.background(Color(0xFF7F0000), shape = RoundedCornerShape(50))) { Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = Color.White) }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onAssignTeacher, modifier = Modifier.weight(1f), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD700)), shape = RoundedCornerShape(20.dp)) { Text("Docente", color = Color(0xFFFFD700), fontSize = 12.sp) }
                OutlinedButton(onClick = onAssignStudents, modifier = Modifier.weight(1f), border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50)), shape = RoundedCornerShape(20.dp)) { Text("Estudiantes", color = Color(0xFF4CAF50), fontSize = 12.sp) }
            }
        }
    }
}
