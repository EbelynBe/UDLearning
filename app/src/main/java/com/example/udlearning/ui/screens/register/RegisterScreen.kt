package com.example.udlearning.ui.screens.register

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
fun RegisterScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("UDLEARNING", fontSize = 28.sp, color = White)

        Spacer(modifier = Modifier.height(40.dp))

        Text("Correo institucional", color = White)
        CustomTextField(email, { email = it }, "Correo")

        Text("Contraseña", color = White)
        CustomTextField(password, { password = it }, "Contraseña", true)

        Text("Confirmar Contraseña", color = White)
        CustomTextField(confirmPassword, { confirmPassword = it }, "Confirmar", true)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Registrar")
        }
    }
}