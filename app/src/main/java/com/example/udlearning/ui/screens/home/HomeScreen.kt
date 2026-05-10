package com.example.udlearning.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.data.UserRepository
import com.example.udlearning.ui.theme.color.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavController) {
    var isTeacher by remember { mutableStateOf<Boolean?>(null) } // null = cargando
    var userName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            UserRepository().getUser(currentUser.uid) { user, _ ->
                userName = user?.nombre ?: "Usuario"
                isTeacher = (user?.rol == "docente" || user?.rol == "profesor")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "¡Hola, $userName!",
                    color = White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isTeacher == true) "Panel de Docente" else "Panel de Estudiante",
                    color = Color(0x99FFFFFF),
                    fontSize = 14.sp
                )
            }
            IconButton(onClick = { navController.navigate("profile") }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil",
                    tint = White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "TU ACTIVIDAD",
            color = Color(0x99FFFFFF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isTeacher == null) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = White)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (isTeacher == true) {
                    HomeCard(
                        title = "Mis sesiones",
                        subtitle = "Gestiona tus clases y grupos",
                        icon = Icons.Default.Class,
                        onClick = { navController.navigate("teacher_sessions") }
                    )
                    HomeCard(
                        title = "Crear sesión",
                        subtitle = "Planifica una nueva clase",
                        icon = Icons.Default.Add,
                        onClick = { navController.navigate("create_session") }
                    )
                } else {
                    HomeCard(
                        title = "Mis sesiones",
                        subtitle = "Ve tus clases programadas",
                        icon = Icons.Default.Book,
                        onClick = { navController.navigate("student_sessions") }
                    )
                }

                HomeCard(
                    title = "Mi Perfil",
                    subtitle = "Configura tu cuenta",
                    icon = Icons.Default.AccountCircle,
                    onClick = { navController.navigate("profile") }
                )
            }
        }
    }
}

@Composable
fun HomeCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC62828))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = Color(0xCCFFFFFF),
                    fontSize = 14.sp
                )
            }
        }
    }
}