@file:OptIn(ExperimentalMaterial3Api::class)

package com.invoicy.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.invoicy.app.data.entity.InvoiceStatus
import com.invoicy.app.data.entity.InvoiceWithDetails
import com.invoicy.app.ui.viewmodel.InvoiceViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Écran optimisé de la liste des factures
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToInvoice: (Long) -> Unit,
    onNavigateToNewInvoice: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    viewModel: InvoiceViewModel = hiltViewModel(),
    settingsViewModel: com.invoicy.app.ui.viewmodel.SettingsViewModel = hiltViewModel()
) {
    val invoices by viewModel.invoices.collectAsState()
    var invoiceToDelete by remember { mutableStateOf<InvoiceWithDetails?>(null) }
    val currency by settingsViewModel.currency.collectAsState()
    var selectedFilter by remember { mutableStateOf("Toutes") }
    
    // Calculs pour la barre de résumé
    val totalInvoiced = invoices.sumOf { it.getTotal() }
    val totalPaid = invoices.filter { it.invoice.status == InvoiceStatus.PAID }.sumOf { it.getTotal() }
    val unpaidCount = invoices.count { it.invoice.status == InvoiceStatus.SENT }
    val overdueCount = invoices.count { it.invoice.status == InvoiceStatus.OVERDUE }
    
    // Filtrage
    val filteredInvoices = when (selectedFilter) {
        "Brouillon" -> invoices.filter { it.invoice.status == InvoiceStatus.DRAFT }
        "Envoyées" -> invoices.filter { it.invoice.status == InvoiceStatus.SENT }
        "Payées" -> invoices.filter { it.invoice.status == InvoiceStatus.PAID }
        "Impayées" -> invoices.filter { it.invoice.status == InvoiceStatus.SENT }
        "En retard" -> invoices.filter { it.invoice.status == InvoiceStatus.OVERDUE }
        else -> invoices
    }
    
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
        ) {
            // Header
            item {
                com.invoicy.app.ui.components.DocumentHeader(
                    title = "Factures",
                    buttonText = "Facture",
                    onAddClick = {
                        viewModel.createNewInvoice()
                        onNavigateToNewInvoice()
                    },
                    onSearchClick = { /* TODO */ },
                    onFilterClick = { /* TODO */ }
                )
            }
            
            // Barre de résumé
            item {
                com.invoicy.app.ui.components.DocumentSummaryBar(
                    stats = listOf(
                        com.invoicy.app.ui.components.SummaryStatItem(
                            title = "Facturé",
                            value = com.invoicy.app.utils.CurrencyFormatter.format(totalInvoiced, currency),
                            icon = Icons.Default.AttachMoney,
                            color = Color(0xFF2D6CDF)
                        ),
                        com.invoicy.app.ui.components.SummaryStatItem(
                            title = "Encaissé",
                            value = com.invoicy.app.utils.CurrencyFormatter.format(totalPaid, currency),
                            icon = Icons.Default.CheckCircle,
                            color = Color(0xFF16A34A)
                        ),
                        com.invoicy.app.ui.components.SummaryStatItem(
                            title = "Impayé",
                            value = unpaidCount.toString(),
                            icon = Icons.Default.Schedule,
                            color = Color(0xFFF59E0B)
                        ),
                        com.invoicy.app.ui.components.SummaryStatItem(
                            title = "Retard",
                            value = overdueCount.toString(),
                            icon = Icons.Default.Warning,
                            color = Color(0xFFDC2626)
                        )
                    )
                )
            }
            
            // Filtres rapides
            item {
                com.invoicy.app.ui.components.StatusFilterChips(
                    filters = listOf("Toutes", "Brouillon", "Envoyées", "Payées", "Impayées", "En retard"),
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }
            
            // État vide
            if (filteredInvoices.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Aucune facture",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                // Liste des factures
                items(filteredInvoices, key = { it.invoice.id }) { invoice ->
                    CompactInvoiceCard(
                        invoice = invoice,
                        currency = currency,
                        onClick = { onNavigateToInvoice(invoice.invoice.id) },
                        onDelete = { invoiceToDelete = invoice },
                        onMarkPaid = { 
                            viewModel.updateInvoiceStatus(invoice.invoice.id, InvoiceStatus.PAID)
                        }
                    )
                }
            }
        }
    }
    
    // Dialog de confirmation de suppression
    invoiceToDelete?.let { invoice ->
        AlertDialog(
            onDismissRequest = { invoiceToDelete = null },
            title = { Text("Supprimer la facture") },
            text = { Text("Êtes-vous sûr de vouloir supprimer la facture ${invoice.invoice.number} ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteInvoice(invoice.invoice)
                        invoiceToDelete = null
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { invoiceToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun CompactInvoiceCard(
    invoice: InvoiceWithDetails,
    currency: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onMarkPaid: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Ligne principale : Numéro + Client
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = invoice.invoice.number,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = invoice.client.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                // Ligne secondaire : Dates + Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Émise: ${dateFormat.format(Date(invoice.invoice.issueDate))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Échéance: ${dateFormat.format(Date(invoice.invoice.dueDate))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    
                    com.invoicy.app.ui.components.StatusBadge(
                        status = com.invoicy.app.ui.components.getInvoiceStatusText(invoice.invoice.status),
                        color = com.invoicy.app.ui.components.getInvoiceStatusColor(invoice.invoice.status)
                    )
                }
            }
            
            // Montant + Menu
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = com.invoicy.app.utils.CurrencyFormatter.format(invoice.getTotal(), currency),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = com.invoicy.app.ui.components.getInvoiceStatusColor(invoice.invoice.status)
                )
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                    }
                    
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        if (invoice.invoice.status != InvoiceStatus.PAID) {
                            DropdownMenuItem(
                                text = { Text("Marquer payée") },
                                onClick = {
                                    onMarkPaid()
                                    showMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Supprimer") },
                            onClick = {
                                onDelete()
                                showMenu = false
                            },
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Delete, 
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                ) 
                            }
                        )
                    }
                }
            }
        }
    }
}
