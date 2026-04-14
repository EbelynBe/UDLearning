package com.example.udlearning.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.ui.components.CustomTextField

@Composable
fun ProfileScreen(navController: NavController) {

    var name by remember { mutableStateOf("Ebelyn") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Editar Perfil", fontSize = 24.sp)

        CustomTextField(name, { name = it }, "Nombre")

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { }) {
            Text("Guardar cambios")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            navController.navigate("login")
        }) {
            Text("Cerrar sesión")
        }
    }
}