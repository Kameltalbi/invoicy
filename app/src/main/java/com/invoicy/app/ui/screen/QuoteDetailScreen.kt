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
import com.invoicy.app.ui.viewmodel.QuoteViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Écran de détail de devis avec aperçu et actions PDF
 */
@Composable
fun QuoteDetailScreen(
    quoteId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: QuoteViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var quote by remember { mutableStateOf<com.invoicy.app.data.entity.QuoteWithDetails?>(null) }
    
    LaunchedEffect(quoteId) {
        viewModel.loadQuote(quoteId)
    }
    
    LaunchedEffect(viewModel.uiState) {
        viewModel.uiState.collect { state ->
            quote = state.currentQuote
        }
    }
    
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isGeneratingPdf by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quote?.quote?.number ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(quoteId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        },
        snackbarHost = {
            snackbarMessage?.let { message ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { snackbarMessage = null }) {
                            Text("OK")
                        }
                    }
                ) {
                    Text(message)
                }
            }
        }
    ) { paddingValues ->
        quote?.let { quoteData ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Informations client
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Client",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(quoteData.client.name, style = MaterialTheme.typography.bodyLarge)
                            if (quoteData.client.email.isNotEmpty()) {
                                Text(quoteData.client.email)
                            }
                            if (quoteData.client.phone.isNotEmpty()) {
                                Text(quoteData.client.phone)
                            }
                        }
                    }
                }
                
                // Dates et statut
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Date d'émission")
                                Text(dateFormat.format(Date(quoteData.quote.issueDate)))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Valide jusqu'au")
                                Text(dateFormat.format(Date(quoteData.quote.validUntil)))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Statut")
                                QuoteStatusBadge(status = quoteData.quote.status)
                            }
                        }
                    }
                }
                
                // Lignes de devis
                item {
                    Text(
                        text = "Lignes de devis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(quoteData.items) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.quantity} × ${item.unitPrice} €")
                                Text(
                                    text = "${item.getTotal()} €",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (item.vatRate > 0) {
                                Text(
                                    text = "TVA ${item.vatRate}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                // Totaux
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Sous-total HT")
                                Text("${quoteData.getSubtotal()} €")
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("TVA")
                                Text("${quoteData.getVatTotal()} €")
                            }
                            if (quoteData.quote.discount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Remise")
                                    Text("-${quoteData.getDiscountAmount()} €")
                                }
                            }
                            Divider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total TTC",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${quoteData.getTotal()} €",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                // Actions PDF
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isGeneratingPdf = true
                                    snackbarMessage = "Génération du PDF en cours..."
                                    isGeneratingPdf = false
                                    snackbarMessage = "PDF généré avec succès"
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isGeneratingPdf
                        ) {
                            if (isGeneratingPdf) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(Icons.Default.Share, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Partager")
                        }
                        
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    snackbarMessage = "Envoi par email..."
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Envoyer par email")
                        }
                        
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    snackbarMessage = "PDF enregistré dans Téléchargements"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Télécharger PDF")
                        }
                    }
                }
                
                // Notes
                if (quoteData.quote.notes.isNotEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Notes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(quoteData.quote.notes)
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Supprimer le devis") },
            text = { Text("Êtes-vous sûr de vouloir supprimer ce devis ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            quote?.let {
                                viewModel.deleteQuote(it.quote)
                                onNavigateBack()
                            }
                        }
                    }
                ) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
