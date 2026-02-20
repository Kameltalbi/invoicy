@file:OptIn(ExperimentalMaterial3Api::class)

package com.invoicy.app.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.invoicy.app.R
import com.invoicy.app.data.entity.QuoteStatus
import com.invoicy.app.data.entity.QuoteWithDetails
import com.invoicy.app.ui.viewmodel.QuoteViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Écran de liste des devis
 */
@Composable
fun QuoteListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQuote: (Long) -> Unit,
    onNavigateToNewQuote: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    viewModel: QuoteViewModel = hiltViewModel(),
    settingsViewModel: com.invoicy.app.ui.viewmodel.SettingsViewModel = hiltViewModel()
) {
    val quotes by viewModel.quotes.collectAsState()
    var quoteToDelete by remember { mutableStateOf<com.invoicy.app.data.entity.QuoteWithDetails?>(null) }
    val currency by settingsViewModel.currency.collectAsState()
    var selectedFilter by remember { mutableStateOf("Tous") }
    
    // Calculs pour la barre de résumé
    val totalQuotes = quotes.size
    val totalAmount = quotes.sumOf { it.getTotal() }
    val acceptedCount = quotes.count { it.quote.status == QuoteStatus.ACCEPTED }
    val pendingCount = quotes.count { it.quote.status == QuoteStatus.SENT }
    
    // Filtrage
    val filteredQuotes = when (selectedFilter) {
        "Brouillons" -> quotes.filter { it.quote.status == QuoteStatus.DRAFT }
        "Envoyés" -> quotes.filter { it.quote.status == QuoteStatus.SENT }
        "Acceptés" -> quotes.filter { it.quote.status == QuoteStatus.ACCEPTED }
        "Refusés" -> quotes.filter { it.quote.status == QuoteStatus.REJECTED }
        else -> quotes
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
                    title = "Devis",
                    buttonText = "Devis",
                    onAddClick = onNavigateToNewQuote,
                    onSearchClick = { /* TODO */ },
                    onFilterClick = { /* TODO */ }
                )
            }
            
            // Barre de résumé
            item {
                com.invoicy.app.ui.components.DocumentSummaryBar(
                    stats = listOf(
                        com.invoicy.app.ui.components.SummaryStatItem(
                            title = "Devis",
                            value = totalQuotes.toString(),
                            icon = Icons.Default.Description,
                            color = Color(0xFF2D6CDF)
                        ),
                        com.invoicy.app.ui.components.SummaryStatItem(
                            title = "Montant",
                            value = com.invoicy.app.utils.CurrencyFormatter.format(totalAmount, currency),
                            icon = Icons.Default.AttachMoney,
                            color = Color(0xFF8B5CF6)
                        ),
                        com.invoicy.app.ui.components.SummaryStatItem(
                            title = "Accepté",
                            value = acceptedCount.toString(),
                            icon = Icons.Default.CheckCircle,
                            color = Color(0xFF16A34A)
                        ),
                        com.invoicy.app.ui.components.SummaryStatItem(
                            title = "Attente",
                            value = pendingCount.toString(),
                            icon = Icons.Default.Schedule,
                            color = Color(0xFFF59E0B)
                        )
                    )
                )
            }
            
            // Filtres rapides
            item {
                com.invoicy.app.ui.components.StatusFilterChips(
                    filters = listOf("Tous", "Brouillons", "Envoyés", "Acceptés", "Refusés"),
                    selectedFilter = selectedFilter,
                    onFilterSelected = { selectedFilter = it }
                )
            }
            
            // État vide
            if (filteredQuotes.isEmpty()) {
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
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Aucun devis",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                // Liste des devis
                items(filteredQuotes, key = { it.quote.id }) { quote ->
                    val scope = rememberCoroutineScope()
                    CompactQuoteCard(
                        quote = quote,
                        currency = currency,
                        onClick = { onNavigateToQuote(quote.quote.id) },
                        onDelete = { quoteToDelete = quote },
                        onConvertToInvoice = {
                            scope.launch {
                                viewModel.convertToInvoice(quote.quote.id)
                            }
                        }
                    )
                }
            }
        }
    }
    
    // Dialog de confirmation de suppression
    quoteToDelete?.let { quote ->
        AlertDialog(
            onDismissRequest = { quoteToDelete = null },
            title = { Text("Supprimer le devis") },
            text = { Text("Êtes-vous sûr de vouloir supprimer le devis ${quote.quote.number} ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteQuote(quote.quote)
                        quoteToDelete = null
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { quoteToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun CompactQuoteCard(
    quote: QuoteWithDetails,
    currency: String,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onConvertToInvoice: () -> Unit
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = quote.quote.number,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = quote.client.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = com.invoicy.app.utils.CurrencyFormatter.format(quote.getTotal(), currency),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = com.invoicy.app.ui.components.getQuoteStatusColor(quote.quote.status)
                    )
                    
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            if (quote.quote.status == QuoteStatus.ACCEPTED) {
                                DropdownMenuItem(
                                    text = { 
                                        Text(
                                            "Convertir en facture", 
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF16A34A)
                                        ) 
                                    },
                                    onClick = {
                                        onConvertToInvoice()
                                        showMenu = false
                                    },
                                    leadingIcon = { 
                                        Icon(
                                            Icons.Default.Transform, 
                                            contentDescription = null,
                                            tint = Color(0xFF16A34A)
                                        ) 
                                    }
                                )
                                Divider()
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
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Validité: ${dateFormat.format(Date(quote.quote.validUntil))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                com.invoicy.app.ui.components.StatusBadge(
                    status = com.invoicy.app.ui.components.getQuoteStatusText(quote.quote.status),
                    color = com.invoicy.app.ui.components.getQuoteStatusColor(quote.quote.status)
                )
            }
        }
    }
}

@Composable
fun QuoteCard(
    quote: com.invoicy.app.data.entity.QuoteWithDetails,
    currency: String,
    dateFormat: SimpleDateFormat,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onConvertToInvoice: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = quote.quote.number,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    QuoteStatusBadge(status = quote.quote.status)
                }
                
                Text(
                    text = quote.client.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = dateFormat.format(Date(quote.quote.issueDate)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = com.invoicy.app.utils.CurrencyFormatter.format(quote.getTotal(), currency),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Voir") },
                        onClick = {
                            onClick()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Visibility, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Modifier") },
                        onClick = {
                            onEdit()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    )
                    
                    DropdownMenuItem(
                        text = { Text("Dupliquer") },
                        onClick = {
                            onDuplicate()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                        }
                    )
                    
                    // Afficher "Convertir en facture" seulement si pas encore converti
                    if (quote.quote.convertedToInvoiceId == null) {
                        DropdownMenuItem(
                            text = { Text("Convertir en facture") },
                            onClick = {
                                onConvertToInvoice()
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Receipt, contentDescription = null)
                            }
                        )
                    }
                    
                    Divider()
                    
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
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun QuoteStatusBadge(status: QuoteStatus) {
    val (text, color) = when (status) {
        QuoteStatus.DRAFT -> stringResource(R.string.status_draft) to MaterialTheme.colorScheme.surfaceVariant
        QuoteStatus.SENT -> stringResource(R.string.status_sent) to MaterialTheme.colorScheme.primary
        QuoteStatus.ACCEPTED -> stringResource(R.string.status_accepted) to MaterialTheme.colorScheme.tertiary
        QuoteStatus.REJECTED -> stringResource(R.string.status_rejected) to MaterialTheme.colorScheme.error
    }
    
    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
