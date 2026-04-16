package com.example.udlearning.ui.screens.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.data.model.Activity
import com.example.udlearning.data.model.Session
import com.example.udlearning.ui.theme.color.*
import androidx.compose.foundation.clickable

@Composable
fun SessionActivitiesScreen(
    navController: NavController,
    sessionId: String,
    viewModel: SessionActivitiesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(sessionId) {
        viewModel.loadSession(sessionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // Reduced header without UDLEARNING since mockup 2 shows just standard header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "‹  UDLEARNING",
                color = White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.popBackStack() }
            )
        }
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0x33FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "▶", color = White, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = viewModel.session?.titulo ?: "Actividades",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = viewModel.session?.nivel ?: "",
                    color = Color(0x99FFFFFF),
                    fontSize = 14.sp
                )
            }
        }

        Divider(color = androidx.compose.ui.graphics.Color(0x33FFFFFF), thickness = 1.dp, modifier = Modifier.padding(bottom = 16.dp))

        Text(
            text = "ACTIVIDADES DE LA SESIÓN",
            color = androidx.compose.ui.graphics.Color(0x99FFFFFF),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (viewModel.isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = White)
            }
        } else if (viewModel.accessError != null) {
            // Blocked access: show informative card
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC62828))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = viewModel.accessError ?: "",
                            color = White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonBackground)
                        ) {
                            Text("Volver", color = DarkGrayText, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else if (viewModel.activities.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No hay actividades registradas.", color = White)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.activities) { act ->
                    ActivityCard(act)
                }
            }
        }
        
        if (viewModel.accessError == null && !viewModel.isLoading) {
            Button(
                onClick = { /* Start session logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonBackground)
            ) {
                Text("Iniciar sesión", color = DarkGrayText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ActivityCard(activity: Activity) {
    val tagColor = when(activity.cantidad) {
        5 -> GreenTag
        else -> OrangeTag
    }
    
    val suffix = when (activity.tipo.lowercase()) {
        "quiz" -> "preguntas"
        "completar" -> "ejercicios"
        "emparejamiento" -> "pares"
        else -> "elementos"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFC62828)) // Transparent dark red
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.titulo,
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = activity.descripcion,
                    color = androidx.compose.ui.graphics.Color(0xCCFFFFFF),
                    fontSize = 14.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(tagColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${activity.cantidad}", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = suffix, color = White, fontSize = 10.sp)
                }
            }
        }
    }
}
