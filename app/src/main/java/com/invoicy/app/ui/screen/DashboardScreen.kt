@file:OptIn(ExperimentalMaterial3Api::class)

package com.invoicy.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.invoicy.app.ui.viewmodel.DashboardViewModel

/**
 * Écran du tableau de bord optimisé
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToInvoices: () -> Unit,
    onNavigateToQuotes: () -> Unit,
    onNavigateToClients: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
    settingsViewModel: com.invoicy.app.ui.viewmodel.SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currency by settingsViewModel.currency.collectAsState()
    var selectedPeriod by remember { mutableStateOf("Ce mois") }
    
    Scaffold { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header avec sélecteur de période
                item {
                    com.invoicy.app.ui.components.DashboardHeader(
                        selectedPeriod = selectedPeriod,
                        onPeriodChange = { selectedPeriod = it },
                        onNavigateToSettings = onNavigateToSettings
                    )
                }
                
                // Ligne 1 : 2 cartes principales (50% chacune)
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Carte CA du mois
                        com.invoicy.app.ui.components.MainStatCard(
                            modifier = Modifier.weight(1f),
                            title = "CA du mois",
                            value = com.invoicy.app.utils.CurrencyFormatter.format(uiState.totalInvoicedThisMonth, currency),
                            comparison = "+12%",
                            comparisonPositive = true,
                            color = Color(0xFF2D6CDF),
                            onClick = onNavigateToInvoices
                        )
                        
                        // Carte Encaissements
                        val paymentRate = if (uiState.totalInvoicedThisMonth > 0) {
                            ((uiState.totalPaidThisMonth / uiState.totalInvoicedThisMonth) * 100).toInt()
                        } else 0
                        
                        com.invoicy.app.ui.components.MainStatCard(
                            modifier = Modifier.weight(1f),
                            title = "Encaissements",
                            value = com.invoicy.app.utils.CurrencyFormatter.format(uiState.totalPaidThisMonth, currency),
                            subtitle = "$paymentRate% payé",
                            color = Color(0xFF16A34A),
                            onClick = onNavigateToInvoices
                        )
                    }
                }
                
                // Ligne 2 : 4 mini cartes en grille 2x2
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            com.invoicy.app.ui.components.MiniStatCard(
                                modifier = Modifier.weight(1f),
                                title = "Factures émises",
                                value = "${uiState.pendingInvoices + uiState.overdueInvoices}",
                                icon = Icons.Default.Receipt,
                                color = Color(0xFF2D6CDF),
                                onClick = onNavigateToInvoices
                            )
                            
                            com.invoicy.app.ui.components.MiniStatCard(
                                modifier = Modifier.weight(1f),
                                title = "Impayées",
                                value = uiState.pendingInvoices.toString(),
                                icon = Icons.Default.Schedule,
                                color = Color(0xFFF59E0B),
                                onClick = onNavigateToInvoices
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            com.invoicy.app.ui.components.MiniStatCard(
                                modifier = Modifier.weight(1f),
                                title = "En retard",
                                value = uiState.overdueInvoices.toString(),
                                icon = Icons.Default.Warning,
                                color = if (uiState.overdueInvoices > 0) Color(0xFFDC2626) else Color(0xFF6B7280),
                                onClick = onNavigateToInvoices
                            )
                            
                            com.invoicy.app.ui.components.MiniStatCard(
                                modifier = Modifier.weight(1f),
                                title = "Devis en attente",
                                value = "0",
                                icon = Icons.Default.Description,
                                color = Color(0xFF8B5CF6),
                                onClick = onNavigateToQuotes
                            )
                        }
                    }
                }
                
                // Séparateur Analyses
                item {
                    Text(
                        text = "Analyses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                
                // Graphiques (inchangés, juste optimisation espacement)
                item {
                    com.invoicy.app.ui.components.MonthlyBarChart(
                        data = uiState.monthlySales,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                
                item {
                    com.invoicy.app.ui.components.CategoryPieChart(
                        data = uiState.salesByCategory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                
                item {
                    com.invoicy.app.ui.components.YearComparisonLineChart(
                        currentYearData = uiState.currentYearSales,
                        previousYearData = uiState.previousYearSales,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                
                item {
                    com.invoicy.app.ui.components.PaidVsUnpaidChart(
                        paidAmount = uiState.totalPaidThisMonth,
                        unpaidAmount = uiState.totalInvoicedThisMonth - uiState.totalPaidThisMonth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
                
                // Espace final
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}
