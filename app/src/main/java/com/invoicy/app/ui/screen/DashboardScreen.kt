@file:OptIn(ExperimentalMaterial3Api::class)

package com.invoicy.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.invoicy.app.R
import com.invoicy.app.ui.viewmodel.DashboardViewModel

/**
 * Écran du tableau de bord
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.dashboard_title)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
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
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.dashboard_this_month),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.dashboard_total_invoiced),
                            value = com.invoicy.app.utils.CurrencyFormatter.format(uiState.totalInvoicedThisMonth, currency),
                            icon = Icons.Default.AttachMoney,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.dashboard_total_paid),
                            value = com.invoicy.app.utils.CurrencyFormatter.format(uiState.totalPaidThisMonth, currency),
                            icon = Icons.Default.CheckCircle,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.dashboard_pending),
                            value = uiState.pendingInvoices.toString(),
                            icon = Icons.Default.Schedule,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.dashboard_overdue),
                            value = uiState.overdueInvoices.toString(),
                            icon = Icons.Default.Warning,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Analyses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Graphique barres - Ventes par mois
                item {
                    com.invoicy.app.ui.components.MonthlyBarChart(
                        data = uiState.monthlySales,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Graphique camembert - Ventes par catégorie
                item {
                    com.invoicy.app.ui.components.CategoryPieChart(
                        data = uiState.salesByCategory,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Graphique courbe - Évolution année actuelle vs année -1
                item {
                    com.invoicy.app.ui.components.YearComparisonLineChart(
                        currentYearData = uiState.currentYearSales,
                        previousYearData = uiState.previousYearSales,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Graphique payé vs non payé
                item {
                    com.invoicy.app.ui.components.PaidVsUnpaidChart(
                        paidAmount = uiState.totalPaidThisMonth,
                        unpaidAmount = uiState.totalInvoicedThisMonth - uiState.totalPaidThisMonth,
                        modifier = Modifier.fillMaxWidth()
                    )
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
