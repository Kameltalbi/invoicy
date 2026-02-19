package com.invoicy.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

/**
 * Graphique en barres des revenus
 */
@Composable
fun RevenueChart(
    data: List<MonthRevenue>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Revenus (6 derniers mois)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        if (data.isEmpty() || data.all { it.amount == 0.0 }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aucune donnée disponible",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            val maxValue = data.maxOfOrNull { it.amount } ?: 1.0
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 16.dp)
            ) {
                val barWidth = size.width / (data.size * 2)
                val spacing = barWidth / 2
                val chartHeight = size.height - 40.dp.toPx()
                
                data.forEachIndexed { index, monthData ->
                    val barHeight = if (maxValue > 0) {
                        (monthData.amount / maxValue * chartHeight).toFloat()
                    } else 0f
                    
                    val x = spacing + (index * (barWidth + spacing))
                    val y = size.height - barHeight - 20.dp.toPx()
                    
                    // Barre
                    drawRoundRect(
                        color = if (monthData.amount > 0) primaryColor else surfaceVariant,
                        topLeft = Offset(x, y),
                        size = Size(barWidth, barHeight),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                }
            }
            
            // Labels des mois
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                data.forEach { monthData ->
                    Text(
                        text = monthData.monthLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Données d'un mois pour le graphique
 */
data class MonthRevenue(
    val month: Int,
    val year: Int,
    val amount: Double,
    val monthLabel: String
) {
    companion object {
        fun getLast6Months(): List<MonthRevenue> {
            val calendar = Calendar.getInstance()
            val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
            
            return (5 downTo 0).map { monthsAgo ->
                calendar.time = Date()
                calendar.add(Calendar.MONTH, -monthsAgo)
                
                MonthRevenue(
                    month = calendar.get(Calendar.MONTH) + 1,
                    year = calendar.get(Calendar.YEAR),
                    amount = 0.0,
                    monthLabel = monthFormat.format(calendar.time)
                )
            }
        }
    }
}
