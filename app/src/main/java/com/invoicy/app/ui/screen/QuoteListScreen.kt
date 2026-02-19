@file:OptIn(ExperimentalMaterial3Api::class)

package com.invoicy.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.invoicy.app.data.entity.QuoteStatus
import com.invoicy.app.ui.viewmodel.QuoteViewModel
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
    viewModel: QuoteViewModel = hiltViewModel()
) {
    val quotes by viewModel.quotes.collectAsState()
    var quoteToDelete by remember { mutableStateOf<com.invoicy.app.data.entity.QuoteWithDetails?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.quotes_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewQuote) {
                Icon(Icons.Default.Add, contentDescription = "New Quote")
            }
        }
    ) { paddingValues ->
        if (quotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.no_quotes),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = onNavigateToNewQuote) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.quote_new))
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(quotes) { quote ->
                    QuoteCard(
                        quote = quote,
                        dateFormat = dateFormat,
                        onClick = { onNavigateToQuote(quote.quote.id) },
                        onEdit = { onNavigateToQuote(quote.quote.id) },
                        onDelete = { quoteToDelete = quote },
                        onDuplicate = { viewModel.duplicateQuote(quote.quote.id) },
                        onConvertToInvoice = { /* TODO: Implement convert */ }
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
fun QuoteCard(
    quote: com.invoicy.app.data.entity.QuoteWithDetails,
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
                        text = "${quote.getTotal()} €",
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
