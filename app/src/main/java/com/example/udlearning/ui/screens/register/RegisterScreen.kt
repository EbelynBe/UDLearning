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
import com.example.udlearning.ui.theme.color.DarkGrayText
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Alignment
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val email = viewModel.email
    val password = viewModel.password
    val confirmPassword = viewModel.confirmPassword
    val name = viewModel.name
    val selectedRole = viewModel.selectedRole
    val selectedGroup = viewModel.selectedGroup
    val availableGroups = viewModel.availableGroups
    
    val registerError = viewModel.errorMessage
    val successMessage = viewModel.successMessage
    val isLoading = viewModel.isLoading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {

        Text("UDLEARNING", fontSize = 28.sp, color = White, modifier = Modifier.padding(bottom = 24.dp))

        Text("Nombre completo", color = White)
        CustomTextField(name, viewModel::onNameChange, "Nombre")


        Text("Correo institucional", color = White)
        CustomTextField(email, viewModel::onEmailChange, "Correo")

        Text("Contraseña", color = White)
        CustomTextField(password, viewModel::onPasswordChange, "Contraseña", true)

        Text("Confirmar Contraseña", color = White)
        CustomTextField(confirmPassword, viewModel::onConfirmPasswordChange, "Confirmar", true)
        
        Spacer(modifier = Modifier.height(16.dp))

        Text("Soy un:", color = White)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedRole == "estudiante",
                onClick = { viewModel.onRoleChange("estudiante") }
            )
            Text("Estudiante", color = White, modifier = Modifier.padding(end = 16.dp))
            
            RadioButton(
                selected = selectedRole == "docente",
                onClick = { viewModel.onRoleChange("docente") }
            )
            Text("Docente", color = White)
        }
        
        if (selectedRole == "estudiante" && availableGroups.isNotEmpty()) {
            Text("Selecciona tu grupo", color = White, modifier = Modifier.padding(bottom = 4.dp))
            var expanded by remember { mutableStateOf(false) }
            
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Button(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = White)
                ) {
                    Text(selectedGroup?.nombre ?: "Seleccionar grupo", color = DarkGrayText)
                }
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    availableGroups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.nombre) },
                            onClick = {
                                viewModel.onGroupChange(group)
                                expanded = false
                            }
                        )
                    }
                }
            }
        } else if (selectedRole == "estudiante") {
            Text("Cargando grupos disponibles...", color = White, fontSize = 12.sp, modifier = Modifier.padding(bottom = 16.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { viewModel.register() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Registrar")
            }
        }

        // 🔥 Mensajes
        registerError?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        successMessage?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(it, color = White)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController()
    RegisterScreen(navController = navController)
}