package com.example.udlearning.ui.screens.graficos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.udlearning.data.model.MetricaProgreso
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

@Composable
fun ProgresoScreen(navController: NavController, viewModel: ProgresoViewModel = viewModel()) {
    val metricas by viewModel.metricas.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadMetricas(currentUserId)
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
            Text("UDLEARNING", color = Color(0xFFFFD700), fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Mi progreso", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (metricas.isEmpty()) {
            Text("No hay datos disponibles", color = Color.White)
        } else {
            val metrica = metricas.first()
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Promedio general", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("${metrica.promedio} / 5.0", color = Color(0xFFFFD700), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Dummy Line Chart
            AndroidView(
                modifier = Modifier.fillMaxWidth().height(250.dp),
                factory = { context ->
                    LineChart(context).apply {
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        
                        // Parse real data from metrica.evolucionTemporal
                        val entries = if (metrica.evolucionTemporal.isNotEmpty()) {
                            metrica.evolucionTemporal.values.mapIndexed { index, value ->
                                Entry((index + 1).toFloat(), value.toFloat())
                            }
                        } else {
                            listOf(Entry(1f, 0f))
                        }
                        
                        val dataSet = LineDataSet(entries, "Evolución").apply { 
                            color = android.graphics.Color.YELLOW
                            valueTextColor = android.graphics.Color.WHITE
                            lineWidth = 3f
                            circleRadius = 5f
                            setCircleColor(android.graphics.Color.YELLOW)
                        }
                        
                        xAxis.textColor = android.graphics.Color.WHITE
                        axisLeft.textColor = android.graphics.Color.WHITE
                        axisRight.isEnabled = false
                        description.isEnabled = false
                        legend.textColor = android.graphics.Color.WHITE
                        
                        data = LineData(dataSet)
                    }
                },
                update = { chart ->
                    chart.invalidate()
                }
            )
        }
    }
}
