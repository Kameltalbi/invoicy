@file:OptIn(ExperimentalMaterial3Api::class)

package com.invoicy.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.invoicy.app.R
import com.invoicy.app.data.entity.*
import com.invoicy.app.ui.viewmodel.ClientViewModel
import com.invoicy.app.ui.viewmodel.InvoiceViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Écran de création/édition de facture
 */
@Composable
fun InvoiceEditScreen(
    invoiceId: Long? = null,
    onNavigateBack: () -> Unit,
    invoiceViewModel: InvoiceViewModel = hiltViewModel(),
    clientViewModel: ClientViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val clients by clientViewModel.clients.collectAsState()
    val uiState by invoiceViewModel.uiState.collectAsState()
    
    var invoiceNumber by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf<Client?>(null) }
    var issueDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var dueDate by remember { mutableStateOf(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) }
    var status by remember { mutableStateOf(InvoiceStatus.DRAFT) }
    var notes by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("0") }
    var items by remember { mutableStateOf(listOf<InvoiceItemData>()) }
    var showClientDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    LaunchedEffect(invoiceId) {
        if (invoiceId != null) {
            invoiceViewModel.loadInvoice(invoiceId)
        } else {
            invoiceNumber = uiState.generatedNumber
        }
    }
    
    LaunchedEffect(uiState.currentInvoice) {
        uiState.currentInvoice?.let { invoice ->
            invoiceNumber = invoice.invoice.number
            selectedClient = invoice.client
            issueDate = invoice.invoice.issueDate
            dueDate = invoice.invoice.dueDate
            status = invoice.invoice.status
            notes = invoice.invoice.notes
            discount = invoice.invoice.discount.toString()
            items = invoice.items.map { item ->
                InvoiceItemData(
                    description = item.description,
                    quantity = item.quantity.toString(),
                    unitPrice = item.unitPrice.toString(),
                    vatRate = item.vatRate.toString()
                )
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (invoiceId == null) stringResource(R.string.invoice_new) 
                         else stringResource(R.string.action_edit)) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (selectedClient == null) {
                                    snackbarMessage = "Veuillez sélectionner un client"
                                    return@launch
                                }
                                if (items.isEmpty()) {
                                    snackbarMessage = "Veuillez ajouter au moins une ligne"
                                    return@launch
                                }
                                
                                val invoice = Invoice(
                                    id = invoiceId ?: 0,
                                    number = invoiceNumber,
                                    clientId = selectedClient!!.id,
                                    issueDate = issueDate,
                                    dueDate = dueDate,
                                    status = status,
                                    notes = notes,
                                    discount = discount.toDoubleOrNull() ?: 0.0
                                )
                                
                                val invoiceItems = items.map { item ->
                                    InvoiceItem(
                                        invoiceId = invoiceId ?: 0,
                                        description = item.description,
                                        quantity = item.quantity.toDoubleOrNull() ?: 1.0,
                                        unitPrice = item.unitPrice.toDoubleOrNull() ?: 0.0,
                                        vatRate = item.vatRate.toDoubleOrNull() ?: 0.0
                                    )
                                }
                                
                                val result = invoiceViewModel.saveInvoice(invoice, invoiceItems)
                                if (result.isSuccess) {
                                    onNavigateBack()
                                } else {
                                    snackbarMessage = "Erreur lors de l'enregistrement"
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = invoiceNumber,
                    onValueChange = { invoiceNumber = it },
                    label = { Text(stringResource(R.string.invoice_number)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )
            }
            
            item {
                OutlinedCard(
                    onClick = { showClientDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.invoice_client),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = selectedClient?.name ?: "Sélectionner un client",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (selectedClient != null) FontWeight.Normal else FontWeight.Light
                            )
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            }
            
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = dateFormat.format(Date(issueDate)),
                        onValueChange = {},
                        label = { Text(stringResource(R.string.invoice_date)) },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        }
                    )
                    
                    OutlinedTextField(
                        value = dateFormat.format(Date(dueDate)),
                        onValueChange = {},
                        label = { Text(stringResource(R.string.invoice_due_date)) },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        }
                    )
                }
            }
            
            item {
                OutlinedCard(
                    onClick = { showStatusDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.invoice_status),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = when (status) {
                                    InvoiceStatus.DRAFT -> stringResource(R.string.status_draft)
                                    InvoiceStatus.SENT -> stringResource(R.string.status_sent)
                                    InvoiceStatus.PAID -> stringResource(R.string.status_paid)
                                    InvoiceStatus.OVERDUE -> stringResource(R.string.status_overdue)
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            }
            
            item {
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lignes de facture",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = {
                            items = items + InvoiceItemData()
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add item")
                    }
                }
            }
            
            itemsIndexed(items) { index, item ->
                InvoiceItemCard(
                    item = item,
                    onItemChange = { newItem ->
                        items = items.toMutableList().apply { set(index, newItem) }
                    },
                    onDelete = {
                        items = items.toMutableList().apply { removeAt(index) }
                    }
                )
            }
            
            item {
                OutlinedTextField(
                    value = discount,
                    onValueChange = { discount = it },
                    label = { Text(stringResource(R.string.invoice_discount) + " (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.invoice_notes)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }
    }
    
    if (showClientDialog) {
        ClientSelectionDialog(
            clients = clients,
            onClientSelected = { client ->
                selectedClient = client
                showClientDialog = false
            },
            onDismiss = { showClientDialog = false }
        )
    }
    
    if (showStatusDialog) {
        StatusSelectionDialog(
            currentStatus = status,
            onStatusSelected = { newStatus ->
                status = newStatus
                showStatusDialog = false
            },
            onDismiss = { showStatusDialog = false }
        )
    }
}

@Composable
fun InvoiceItemCard(
    item: InvoiceItemData,
    onItemChange: (InvoiceItemData) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ligne de prestation",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            OutlinedTextField(
                value = item.description,
                onValueChange = { onItemChange(item.copy(description = it)) },
                label = { Text(stringResource(R.string.item_description)) },
                modifier = Modifier.fillMaxWidth()
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = item.quantity,
                    onValueChange = { onItemChange(item.copy(quantity = it)) },
                    label = { Text(stringResource(R.string.item_quantity)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                OutlinedTextField(
                    value = item.unitPrice,
                    onValueChange = { onItemChange(item.copy(unitPrice = it)) },
                    label = { Text(stringResource(R.string.item_unit_price)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                
                OutlinedTextField(
                    value = item.vatRate,
                    onValueChange = { onItemChange(item.copy(vatRate = it)) },
                    label = { Text(stringResource(R.string.item_vat) + " %") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        }
    }
}

@Composable
fun ClientSelectionDialog(
    clients: List<Client>,
    onClientSelected: (Client) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.invoice_client)) },
        text = {
            LazyColumn {
                items(clients.size) { index ->
                    val client = clients[index]
                    TextButton(
                        onClick = { onClientSelected(client) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = client.name,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun StatusSelectionDialog(
    currentStatus: InvoiceStatus,
    onStatusSelected: (InvoiceStatus) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.invoice_status)) },
        text = {
            Column {
                InvoiceStatus.values().forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = status == currentStatus,
                            onClick = { onStatusSelected(status) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (status) {
                                InvoiceStatus.DRAFT -> stringResource(R.string.status_draft)
                                InvoiceStatus.SENT -> stringResource(R.string.status_sent)
                                InvoiceStatus.PAID -> stringResource(R.string.status_paid)
                                InvoiceStatus.OVERDUE -> stringResource(R.string.status_overdue)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

data class InvoiceItemData(
    val description: String = "",
    val quantity: String = "1",
    val unitPrice: String = "0",
    val vatRate: String = "20"
)
