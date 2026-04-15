package com.example.udlearning.ui.screens.login



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.ui.components.CustomTextField
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.navigation.compose.rememberNavController
import com.example.udlearning.ui.theme.color.RedPrimary
import com.example.udlearning.ui.theme.color.White

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val email = viewModel.email
    val password = viewModel.password
    val loginError = viewModel.errorMessage
    val isLoading = viewModel.isLoading

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
        CustomTextField(email, viewModel::onEmailChange, "Correo")

        Text("Contraseña", color = White)
        CustomTextField(password, viewModel::onPasswordChange, "Contraseña", true)

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.login {
                    navController.navigate("home")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Ingresar")
            }
        }

        // Mostrar error
        loginError?.let {
            Spacer(modifier = Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("REGISTRARSE", color = White,
            modifier = Modifier.clickable {
                navController.navigate("register")
            })

        Text("OLVIDÉ MI CONTRASEÑA", color = White,
            modifier = Modifier.clickable {
                navController.navigate("recovery")
            })
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginScreen(navController = navController)
}