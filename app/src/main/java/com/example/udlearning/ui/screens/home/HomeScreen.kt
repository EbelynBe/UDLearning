package com.example.udlearning.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController



import androidx.compose.runtime.*
import com.example.udlearning.data.UserRepository
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavController) {

    var isTeacher by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            UserRepository().getUser(currentUser.uid) { user, _ ->
                if (user?.rol == "docente") {
                    isTeacher = true
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Resumen del día", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        if (isTeacher) {
            Button(onClick = { navController.navigate("create_session") }) {
                Text("Crear sesión")
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.navigate("teacher_sessions") }) {
                Text("Mis sesiones (Docente)")
            }
    
            Spacer(modifier = Modifier.height(16.dp))
        } else {
            // Estudiante
            Button(onClick = { navController.navigate("student_sessions") }) {
                Text("Mis sesiones (Estudiante)")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.navigate("profile") }) {
            Text("Ir a perfil")
        }
    }
}