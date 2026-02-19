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
import com.invoicy.app.ui.viewmodel.ProductViewModel
import com.invoicy.app.ui.viewmodel.QuoteViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Écran de création/édition de devis
 */
@Composable
fun QuoteEditScreen(
    quoteId: Long? = null,
    onNavigateBack: () -> Unit,
    quoteViewModel: QuoteViewModel = hiltViewModel(),
    clientViewModel: ClientViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val clients by clientViewModel.clients.collectAsState()
    val products by productViewModel.products.collectAsState()
    val uiState by quoteViewModel.uiState.collectAsState()
    
    var quoteNumber by remember { mutableStateOf("") }
    var selectedClient by remember { mutableStateOf<Client?>(null) }
    var issueDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var validUntil by remember { mutableStateOf(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000) }
    var status by remember { mutableStateOf(QuoteStatus.DRAFT) }
    var notes by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("0") }
    var items by remember { mutableStateOf(listOf<QuoteItemData>()) }
    var showClientDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showProductDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    LaunchedEffect(quoteId) {
        if (quoteId != null) {
            quoteViewModel.loadQuote(quoteId)
        } else {
            quoteNumber = uiState.generatedNumber
        }
    }
    
    LaunchedEffect(uiState.currentQuote) {
        uiState.currentQuote?.let { quote ->
            quoteNumber = quote.quote.number
            selectedClient = quote.client
            issueDate = quote.quote.issueDate
            validUntil = quote.quote.validUntil
            status = quote.quote.status
            notes = quote.quote.notes
            discount = quote.quote.discount.toString()
            items = quote.items.map { item ->
                QuoteItemData(
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
                    Text(if (quoteId == null) stringResource(R.string.quote_new) 
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
                                
                                val quote = Quote(
                                    id = quoteId ?: 0,
                                    number = quoteNumber,
                                    clientId = selectedClient!!.id,
                                    issueDate = issueDate,
                                    validUntil = validUntil,
                                    status = status,
                                    notes = notes,
                                    discount = discount.toDoubleOrNull() ?: 0.0
                                )
                                
                                val quoteItems = items.map { item ->
                                    QuoteItem(
                                        quoteId = quoteId ?: 0,
                                        description = item.description,
                                        quantity = item.quantity.toDoubleOrNull() ?: 1.0,
                                        unitPrice = item.unitPrice.toDoubleOrNull() ?: 0.0,
                                        vatRate = item.vatRate.toDoubleOrNull() ?: 0.0
                                    )
                                }
                                
                                val result = quoteViewModel.saveQuote(quote, quoteItems)
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
                    value = quoteNumber,
                    onValueChange = { quoteNumber = it },
                    label = { Text(stringResource(R.string.quote_number)) },
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
                                text = stringResource(R.string.quote_client),
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
                        label = { Text(stringResource(R.string.quote_date)) },
                        modifier = Modifier.weight(1f),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        }
                    )
                    
                    OutlinedTextField(
                        value = dateFormat.format(Date(validUntil)),
                        onValueChange = {},
                        label = { Text(stringResource(R.string.quote_valid_until)) },
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
                                text = stringResource(R.string.quote_status),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = when (status) {
                                    QuoteStatus.DRAFT -> stringResource(R.string.status_draft)
                                    QuoteStatus.SENT -> stringResource(R.string.status_sent)
                                    QuoteStatus.ACCEPTED -> stringResource(R.string.status_accepted)
                                    QuoteStatus.REJECTED -> stringResource(R.string.status_rejected)
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
                        text = "Lignes de devis",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        IconButton(
                            onClick = { showProductDialog = true }
                        ) {
                            Icon(Icons.Default.Inventory, contentDescription = "Ajouter depuis catalogue")
                        }
                        IconButton(
                            onClick = {
                                items = items + QuoteItemData()
                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Ajouter ligne vide")
                        }
                    }
                }
            }
            
            itemsIndexed(items) { index, item ->
                QuoteItemCard(
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
                    label = { Text(stringResource(R.string.quote_discount) + " (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
            
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.quote_notes)) },
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
        QuoteStatusSelectionDialog(
            currentStatus = status,
            onStatusSelected = { newStatus ->
                status = newStatus
                showStatusDialog = false
            },
            onDismiss = { showStatusDialog = false }
        )
    }
    
    if (showProductDialog) {
        ProductSelectionDialog(
            products = products,
            onProductSelected = { product ->
                items = items + QuoteItemData(
                    description = product.product.name,
                    quantity = "1",
                    unitPrice = product.product.unitPrice.toString(),
                    vatRate = product.product.vatRate.toString()
                )
                showProductDialog = false
            },
            onDismiss = { showProductDialog = false }
        )
    }
}

@Composable
fun QuoteItemCard(
    item: QuoteItemData,
    onItemChange: (QuoteItemData) -> Unit,
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
fun QuoteStatusSelectionDialog(
    currentStatus: QuoteStatus,
    onStatusSelected: (QuoteStatus) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.quote_status)) },
        text = {
            Column {
                QuoteStatus.values().forEach { status ->
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
                                QuoteStatus.DRAFT -> stringResource(R.string.status_draft)
                                QuoteStatus.SENT -> stringResource(R.string.status_sent)
                                QuoteStatus.ACCEPTED -> stringResource(R.string.status_accepted)
                                QuoteStatus.REJECTED -> stringResource(R.string.status_rejected)
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

data class QuoteItemData(
    val description: String = "",
    val quantity: String = "1",
    val unitPrice: String = "0",
    val vatRate: String = "20"
)

@Composable
fun ProductSelectionDialog(
    products: List<com.invoicy.app.data.entity.ProductWithCategory>,
    onProductSelected: (com.invoicy.app.data.entity.ProductWithCategory) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sélectionner un produit") },
        text = {
            if (products.isEmpty()) {
                Text("Aucun produit disponible. Créez d'abord des produits dans le catalogue.")
            } else {
                LazyColumn {
                    items(products.size) { index ->
                        val product = products[index]
                        Card(
                            onClick = { onProductSelected(product) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.product.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = product.category.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "${product.product.unitPrice} €",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
