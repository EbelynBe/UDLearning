package com.example.udlearning.ui.screens.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.data.model.Session
import com.example.udlearning.ui.components.TitleHeader
import com.example.udlearning.ui.theme.color.*
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun StudentSessionsScreen(
    navController: NavController,
    viewModel: StudentSessionsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(top = 24.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            TitleHeader(onBackClick = { navController.popBackStack() })

            Text(
                text = "Mis sesiones",
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Filtering Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("Todas", "Activas", "Pendientes", "Finalizadas", "Archivadas")
                filters.forEach { filter ->
                    FilterChip(
                        title = filter,
                        isSelected = filter == viewModel.currentFilter,
                        onClick = { viewModel.applyFilter(filter) }
                    )
                }
            }
        }

        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = White)
            }
        } else if (viewModel.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(viewModel.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            }
        } else if (viewModel.filteredSessions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Text("No se encontraron sesiones.", color = White)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.filteredSessions) { session ->
                    SessionCard(session) {
                        navController.navigate("session_activities/${session.sessionId}")
                    }
                }
                item {
                    Text(
                        text = "${viewModel.filteredSessions.size} sesiones encontradas",
                        color = Color(0x99FFFFFF), // Semi-transparent white
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChip(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) Color(0xFFE53935) else Color.Transparent) // Highlight red
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = White,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SessionCard(session: Session, onClick: () -> Unit) {
    val statusColor = when (session.estado.lowercase()) {
        "activa"     -> GreenTag
        "pendiente"  -> OrangeTag
        "finalizada" -> GrayTag
        "archivada"  -> Color(0xFF455A64) // blue-gray
        else         -> GrayTag
    }
    
    val statusText = session.estado.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    val formatter = SimpleDateFormat("dd MMM", Locale("es", "ES"))
    val startDateStr = session.fechaInicio?.toDate()?.let { formatter.format(it) } ?: "N/A"
    val endDateStr = session.fechaFin?.toDate()?.let { formatter.format(it) } ?: "N/A"
    
    // Attempting to match formatting: "Inglés I - 20-26 Abr"
    val subtitleText = "${session.nivel} · $startDateStr - $endDateStr"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC62828)) // Darker red background
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
                    text = session.titulo,
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = subtitleText,
                    color = Color(0xCCFFFFFF),
                    fontSize = 14.sp
                )
            }
            
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusText,
                    color = White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
