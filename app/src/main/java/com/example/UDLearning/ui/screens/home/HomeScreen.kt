package com.example.UDLearning.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController



@Composable
fun HomeScreen(navController: NavController) {

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Resumen del día", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))




        Button(onClick = { navController.navigate("profile") }) {
            Text("Ir a perfil")
        }
    }
}