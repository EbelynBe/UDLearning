package com.example.udlearning.ui.screens.usuarios

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
import com.example.udlearning.data.model.User
import java.util.Locale

@Composable
fun UsuariosScreen(navController: NavController, viewModel: UsuariosViewModel = viewModel()) {
    val usuarios by viewModel.usuarios.collectAsState()

    var userToEdit by remember { mutableStateOf<User?>(null) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadUsuarios()
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
        Text("Gestionar usuarios", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (usuarios.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("Cargando usuarios...", color = Color.White)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(usuarios) { user ->
                    UserCard(
                        user = user,
                        onEdit = { userToEdit = user },
                        onDelete = { userToDelete = user }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = Color.LightGray)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Nombre completo", color = Color.White)
        OutlinedTextField(
            value = "", onValueChange = {}, modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.LightGray,
                focusedContainerColor = Color.LightGray
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Correo electrónico", color = Color.White)
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
            Text("+ Crear usuario", color = Color.Black)
        }
    }

    // Dialogo de Editar Rol
    if (userToEdit != null) {
        EditRoleDialog(
            user = userToEdit!!,
            onDismiss = { userToEdit = null },
            onConfirm = { newRole ->
                viewModel.updateUserRole(userToEdit!!, newRole)
                userToEdit = null
            }
        )
    }

    // Dialogo de Confirmar Eliminación
    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            containerColor = Color(0xFF2C2C2C),
            title = {
                Text("Eliminar usuario", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar a ${userToDelete!!.nombre}? Esta acción no se puede deshacer.",
                    color = Color.LightGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteUser(userToDelete!!.userId)
                        userToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("Cancelar", color = Color.LightGray)
                }
            }
        )
    }
}

@Composable
fun EditRoleDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val roles = listOf("estudiante", "docente", "administrador")
    var selectedRole by remember { mutableStateOf(user.rol.lowercase(Locale.getDefault())) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF2C2C2C),
        title = {
            Text("Editar rol de ${user.nombre}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column {
                Text("Selecciona el nuevo rol:", color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                roles.forEach { role ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRole = role }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = (selectedRole == role),
                            onClick = { selectedRole = role },
                            colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFD700))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = role.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            color = Color.White
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedRole) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
            ) {
                Text("Guardar", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.LightGray)
            }
        }
    )
}

@Composable
fun UserCard(user: User, onEdit: () -> Unit, onDelete: () -> Unit) {
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
                Text(user.nombre, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(user.email, color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                
                // Formatear el rol para que se vea bien (ej: Administrador, Estudiante)
                val rolFormat = user.rol.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                
                Text("Rol: $rolFormat", color = Color(0xFFFFD700), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Row {
                IconButton(onClick = onEdit, modifier = Modifier.background(Color(0xFF1976D2), shape = RoundedCornerShape(50))) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onDelete, modifier = Modifier.background(Color(0xFF7F0000), shape = RoundedCornerShape(50))) {
                    Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = Color.White)
                }
            }
        }
    }
}
