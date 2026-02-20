package com.invoicy.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Header du Dashboard avec sélecteur de période
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardHeader(
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var showPeriodMenu by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Tableau de bord",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            // Sélecteur de période
            Box {
                OutlinedButton(
                    onClick = { showPeriodMenu = true },
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = selectedPeriod,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                DropdownMenu(
                    expanded = showPeriodMenu,
                    onDismissRequest = { showPeriodMenu = false }
                ) {
                    listOf("Ce mois", "Mois précédent", "Cette année").forEach { period ->
                        DropdownMenuItem(
                            text = { Text(period) },
                            onClick = {
                                onPeriodChange(period)
                                showPeriodMenu = false
                            }
                        )
                    }
                }
            }
        }
        
        IconButton(onClick = onNavigateToSettings) {
            Icon(Icons.Default.Settings, contentDescription = "Paramètres")
        }
    }
}

/**
 * Carte principale de statistique (50% largeur)
 */
@Composable
fun MainStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    comparison: String? = null,
    comparisonPositive: Boolean = true,
    subtitle: String? = null,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    comparison?.let {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (comparisonPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (comparisonPositive) Color(0xFF16A34A) else Color(0xFFDC2626)
                            )
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (comparisonPositive) Color(0xFF16A34A) else Color(0xFFDC2626),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Mini carte de statistique compacte (25% largeur en grille 2x2)
 */
@Composable
fun MiniStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .height(90.dp)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = color.copy(alpha = 0.7f)
                )
            }
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
