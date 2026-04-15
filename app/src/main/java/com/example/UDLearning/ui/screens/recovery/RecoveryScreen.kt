package com.example.UDLearning.ui.screens.recovery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.UDLearning.ui.components.CustomTextField

import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.UDLearning.ui.theme.color.RedPrimary
import com.example.UDLearning.ui.theme.color.White
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun RecoveryScreen(
    navController: NavController,
    viewModel: RecoveryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val email = viewModel.email
    val error = viewModel.errorMessage
    val success = viewModel.successMessage
    val loading = viewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Recuperar contraseña", color = White, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Text("Correo institucional", color = White)
        CustomTextField(email, viewModel::onEmailChange, "Correo")

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.sendRecoveryEmail() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator()
            } else {
                Text("Enviar enlace")
            }
        }

        // 🔥 Mensajes
        error?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        success?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(it, color = White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("VOLVER AL LOGIN", color = White,
            modifier = Modifier.clickable {
                navController.navigate("login")
            })
    }
}

@Preview(name = "Modo Claro", showBackground = true)
@Preview(
    name = "Modo Oscuro",
    showBackground = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun RecoveryScreenPreview() {
    val navController = rememberNavController()
    RecoveryScreen(navController = navController)
}