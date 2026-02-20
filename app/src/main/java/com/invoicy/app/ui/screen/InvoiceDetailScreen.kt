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
import com.invoicy.app.ui.viewmodel.InvoiceViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Écran de détail de facture avec aperçu et actions
 */
@Composable
fun InvoiceDetailScreen(
    invoiceId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: InvoiceViewModel = hiltViewModel(),
    settingsViewModel: com.invoicy.app.ui.viewmodel.SettingsViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var invoice by remember { mutableStateOf<com.invoicy.app.data.entity.InvoiceWithDetails?>(null) }
    val currency by settingsViewModel.currency.collectAsState()
    
    LaunchedEffect(invoiceId) {
        viewModel.loadInvoice(invoiceId)
    }
    
    LaunchedEffect(viewModel.uiState) {
        viewModel.uiState.collect { state ->
            invoice = state.currentInvoice
        }
    }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isGeneratingPdf by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(invoice?.invoice?.number ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(invoiceId) }) {
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
        invoice?.let { invoiceData ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.invoice_client),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = invoiceData.client.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = invoiceData.client.email)
                            Text(text = invoiceData.client.phone)
                        }
                    }
                }
                
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.invoice_date),
                            value = dateFormat.format(Date(invoiceData.invoice.issueDate))
                        )
                        InfoCard(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.invoice_due_date),
                            value = dateFormat.format(Date(invoiceData.invoice.dueDate))
                        )
                    }
                }
                
                item {
                    Text(
                        text = "Lignes de facture",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                items(invoiceData.items) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.quantity} × ${com.invoicy.app.utils.CurrencyFormatter.format(item.unitPrice, currency)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "TVA ${item.vatRate}%",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                text = com.invoicy.app.utils.CurrencyFormatter.format(item.getTotal(), currency),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                
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
                                Text(stringResource(R.string.subtotal))
                                Text(com.invoicy.app.utils.CurrencyFormatter.format(invoiceData.getSubtotal(), currency))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(stringResource(R.string.vat_total))
                                Text(com.invoicy.app.utils.CurrencyFormatter.format(invoiceData.getVatTotal(), currency))
                            }
                            if (invoiceData.invoice.discount > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(stringResource(R.string.invoice_discount))
                                    Text("-${com.invoicy.app.utils.CurrencyFormatter.format(invoiceData.getDiscountAmount(), currency)}")
                                }
                            }
                            Divider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.total),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = com.invoicy.app.utils.CurrencyFormatter.format(invoiceData.getTotal(), currency),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
                
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isGeneratingPdf = true
                                    val result = viewModel.generatePdf(invoiceId)
                                    isGeneratingPdf = false
                                    result.onSuccess { file ->
                                        viewModel.sharePdf(file)
                                    }.onFailure {
                                        snackbarMessage = "Erreur lors de la génération du PDF"
                                    }
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
                            Text(stringResource(R.string.action_share))
                        }
                        
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val result = viewModel.generatePdf(invoiceId)
                                    result.onSuccess { file ->
                                        viewModel.sendPdfByEmail(file, invoiceData.invoice.number)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.action_send_email))
                        }
                        
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val result = viewModel.generatePdf(invoiceId)
                                    result.onSuccess { file ->
                                        if (viewModel.savePdfToDownloads(file)) {
                                            snackbarMessage = "PDF enregistré dans Téléchargements"
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.action_download))
                        }
                    }
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.action_delete)) },
            text = { Text(stringResource(R.string.msg_confirm_delete)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        invoice?.let {
                            viewModel.deleteInvoice(it.invoice)
                            onNavigateBack()
                        }
                    }
                ) {
                    Text(stringResource(R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

