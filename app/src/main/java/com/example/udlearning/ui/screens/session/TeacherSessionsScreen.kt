package com.example.udlearning.ui.screens.session

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.udlearning.ui.components.TitleHeader
import com.example.udlearning.ui.theme.color.*

@Composable
fun TeacherSessionsScreen(
    navController: NavController,
    viewModel: TeacherSessionsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Reload dynamically on every entry
    LaunchedEffect(Unit) {
        viewModel.fetchTeacherSessions()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RedPrimary)
            .padding(top = 24.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            TitleHeader(onBackClick = { navController.popBackStack() })

            Text(
                text = "Tus sesiones",
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
                val filters = listOf("Todas", "Pendientes", "Activas", "Próximas", "Finalizadas")
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
                Text("No se encontraron sesiones creadas por ti.", color = White)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.filteredSessions) { session ->
                    SessionCard(session) {
                        navController.navigate("manage_session/${session.sessionId}")
                    }
                }
                item {
                    Text(
                        text = "${viewModel.filteredSessions.size} sesiones encontradas",
                        color = androidx.compose.ui.graphics.Color(0x99FFFFFF), 
                        fontSize = 12.sp,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 24.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}
