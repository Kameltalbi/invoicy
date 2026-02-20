package com.invoicy.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Graphique en barres pour les ventes par mois
 */
@Composable
fun MonthlyBarChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    title: String = "Ventes par mois"
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1000),
        label = "bar_animation"
    )
    
    LaunchedEffect(Unit) {
        animationPlayed = true
    }
    
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune donnée",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                val maxValue = data.maxOfOrNull { it.second } ?: 1.0
                
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    val barWidth = size.width / (data.size * 2f)
                    val spacing = barWidth * 0.3f
                    
                    data.forEachIndexed { index, (month, value) ->
                        val barHeight = (value / maxValue) * size.height * 0.8f * animatedProgress
                        val x = index * (barWidth + spacing) + spacing
                        
                        // Barre
                        drawRect(
                            color = Color(0xFF2D6CDF),
                            topLeft = Offset(x, size.height - barHeight.toFloat()),
                            size = Size(barWidth, barHeight.toFloat())
                        )
                        
                        // Label mois
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                month,
                                x + barWidth / 2,
                                size.height - 5,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.GRAY
                                    textSize = 24f
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Graphique camembert pour les ventes par catégorie
 */
@Composable
fun CategoryPieChart(
    data: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    title: String = "Ventes par catégorie"
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1000),
        label = "pie_animation"
    )
    
    LaunchedEffect(Unit) {
        animationPlayed = true
    }
    
    val colors = listOf(
        Color(0xFF2D6CDF),
        Color(0xFF16A34A),
        Color(0xFFF59E0B),
        Color(0xFFDC2626),
        Color(0xFF8B5CF6),
        Color(0xFF06B6D4)
    )
    
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (data.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune donnée",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                val total = data.sumOf { it.second }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Camembert
                    Canvas(
                        modifier = Modifier
                            .size(150.dp)
                            .weight(1f)
                    ) {
                        val radius = size.minDimension / 2
                        val center = Offset(size.width / 2, size.height / 2)
                        var startAngle = -90f
                        
                        data.forEachIndexed { index, (_, value) ->
                            val sweepAngle = ((value / total) * 360f * animatedProgress).toFloat()
                            val color = colors[index % colors.size]
                            
                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2)
                            )
                            
                            startAngle += sweepAngle
                        }
                    }
                    
                    // Légende
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        data.take(6).forEachIndexed { index, (category, value) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(colors[index % colors.size])
                                )
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Graphique en courbe pour l'évolution année actuelle vs année -1
 */
@Composable
fun YearComparisonLineChart(
    currentYearData: List<Pair<String, Double>>,
    previousYearData: List<Pair<String, Double>>,
    modifier: Modifier = Modifier,
    title: String = "Évolution annuelle"
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1000),
        label = "line_animation"
    )
    
    LaunchedEffect(Unit) {
        animationPlayed = true
    }
    
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            // Légende
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF2D6CDF))
                    )
                    Text("2026", style = MaterialTheme.typography.bodySmall)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Color(0xFF94A3B8))
                    )
                    Text("2025", style = MaterialTheme.typography.bodySmall)
                }
            }
            
            if (currentYearData.isEmpty() && previousYearData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune donnée",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                val maxValue = maxOf(
                    currentYearData.maxOfOrNull { it.second } ?: 0.0,
                    previousYearData.maxOfOrNull { it.second } ?: 0.0,
                    1.0
                )
                
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    val stepX = size.width / 11f
                    val scaleY = size.height * 0.8f / maxValue.toFloat()
                    
                    // Ligne année actuelle
                    if (currentYearData.isNotEmpty()) {
                        val path = Path()
                        currentYearData.forEachIndexed { index, (_, value) ->
                            val x = index * stepX
                            val y = size.height - (value * scaleY * animatedProgress).toFloat()
                            
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }
                        
                        drawPath(
                            path = path,
                            color = Color(0xFF2D6CDF),
                            style = Stroke(width = 4f)
                        )
                    }
                    
                    // Ligne année précédente
                    if (previousYearData.isNotEmpty()) {
                        val path = Path()
                        previousYearData.forEachIndexed { index, (_, value) ->
                            val x = index * stepX
                            val y = size.height - (value * scaleY * animatedProgress).toFloat()
                            
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }
                        
                        drawPath(
                            path = path,
                            color = Color(0xFF94A3B8),
                            style = Stroke(width = 4f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Graphique montants payés vs non payés
 */
@Composable
fun PaidVsUnpaidChart(
    paidAmount: Double,
    unpaidAmount: Double,
    modifier: Modifier = Modifier,
    title: String = "Payé vs Non payé"
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(1000),
        label = "paid_animation"
    )
    
    LaunchedEffect(Unit) {
        animationPlayed = true
    }
    
    val total = paidAmount + unpaidAmount
    val paidPercentage = if (total > 0) (paidAmount / total * 100).toInt() else 0
    
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Graphique circulaire
                Canvas(
                    modifier = Modifier.size(120.dp)
                ) {
                    val radius = size.minDimension / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    val strokeWidth = 20f
                    
                    // Cercle de fond
                    drawCircle(
                        color = Color(0xFFE2E8F0),
                        radius = radius - strokeWidth / 2,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )
                    
                    // Arc payé
                    if (total > 0) {
                        val sweepAngle = (paidAmount / total * 360f * animatedProgress).toFloat()
                        drawArc(
                            color = Color(0xFF16A34A),
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(center.x - radius + strokeWidth / 2, center.y - radius + strokeWidth / 2),
                            size = Size((radius - strokeWidth / 2) * 2, (radius - strokeWidth / 2) * 2),
                            style = Stroke(width = strokeWidth)
                        )
                    }
                    
                    // Pourcentage au centre
                    drawContext.canvas.nativeCanvas.apply {
                        drawText(
                            "$paidPercentage%",
                            center.x,
                            center.y + 15,
                            android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor("#16A34A")
                                textSize = 36f
                                textAlign = android.graphics.Paint.Align.CENTER
                                isFakeBoldText = true
                            }
                        )
                    }
                }
                
                // Statistiques
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFF16A34A))
                            )
                            Text(
                                text = "Payé",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = String.format("%.2f TND", paidAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF16A34A)
                        )
                    }
                    
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color(0xFFE2E8F0))
                            )
                            Text(
                                text = "Non payé",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Text(
                            text = String.format("%.2f TND", unpaidAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}
