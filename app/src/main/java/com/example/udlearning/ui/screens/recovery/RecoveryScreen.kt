package com.example.udlearning.ui.screens.recovery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.ui.components.CustomTextField

import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.udlearning.ui.theme.color.RedPrimary
import com.example.udlearning.ui.theme.color.White

@Composable
fun RecoveryScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Restauración de contraseña", color = White, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Text("Correo institucional", color = White)
        CustomTextField(email, { email = it }, "Correo")

        Text("Nueva Contraseña", color = White)
        CustomTextField(newPassword, { newPassword = it }, "Nueva contraseña", true)

        Text("Confirmar Contraseña", color = White)
        CustomTextField(confirmPassword, { confirmPassword = it }, "Confirmar", true)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Cambiar contraseña")
        }
    }
}