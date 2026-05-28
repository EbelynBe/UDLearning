package com.example.udlearning.ui.screens.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.data.model.HistorialEstudiante
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HistorialScreen(navController: NavController, viewModel: HistorialViewModel = viewModel()) {
    val historial by viewModel.historial.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadHistorial(currentUserId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0392B))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "UDLEARNING",
                color = Color(0xFFFFD700),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Mi historial", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))

        if (historial.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes actividades registradas", color = Color.White)
            }
        } else {
            LazyColumn {
                items(historial) { item ->
                    HistorialItem(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun HistorialItem(item: HistorialEstudiante) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(item.nombreActividad, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                val badgeColor = if (item.estado == "Completada") Color(0xFF2E7D32) else Color(0xFFE65100)
                Surface(color = badgeColor, shape = RoundedCornerShape(8.dp)) {
                    Text(item.estado, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("${item.asignatura} - ${item.tipoRegistro}", color = Color.LightGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Calificación: ${item.calificacion} / 5.0", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
        }
    }
}
