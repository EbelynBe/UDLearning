package com.example.udlearning.ui.screens.estadisticas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun EstadisticasGrupalScreen(navController: NavController, viewModel: EstadisticasGrupalViewModel = viewModel()) {
    val estadisticas by viewModel.estadisticas.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadEstadisticas() }
    
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
        Text("Estadísticas grupales", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (estadisticas.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("No tienes grupos asignados a tu cargo.", color = Color.White)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(estadisticas) { stat ->
                    EstadisticaCard(stat)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun EstadisticaCard(stat: com.example.udlearning.data.model.EstadisticaGrupal) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stat.nombreGrupo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(stat.asignatura, color = Color.LightGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("Promedio Grupal", color = Color.LightGray, fontSize = 12.sp)
                    Text("${stat.promedioGrupal} / 5.0", color = Color(0xFFFFD700), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Estudiantes", color = Color.LightGray, fontSize = 12.sp)
                    Text("${stat.totalEstudiantes}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Temas más difíciles:", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            stat.temasMasDificiles.forEach { tema ->
                Text("• $tema", color = Color.LightGray, fontSize = 14.sp)
            }
        }
    }
}
