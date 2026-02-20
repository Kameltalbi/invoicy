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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.invoicy.app.data.entity.Tax
import com.invoicy.app.data.entity.TaxType
import com.invoicy.app.ui.viewmodel.TaxViewModel

/**
 * Écran de gestion des taxes personnalisées
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: TaxViewModel = hiltViewModel()
) {
    val taxes by viewModel.taxes.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var taxToEdit by remember { mutableStateOf<Tax?>(null) }
    var taxToDelete by remember { mutableStateOf<Tax?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Taxes personnalisées") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter une taxe")
            }
        }
    ) { padding ->
        if (taxes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Aucune taxe configurée",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Créez des taxes personnalisées pour vos factures et devis",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(taxes) { tax ->
                    TaxCard(
                        tax = tax,
                        onEdit = { taxToEdit = tax },
                        onDelete = { taxToDelete = tax },
                        onToggleActive = { viewModel.toggleTaxActive(tax) }
                    )
                }
            }
        }
    }
    
    if (showAddDialog) {
        TaxDialog(
            tax = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, type, value, applyToInvoices, applyToQuotes ->
                viewModel.createTax(name, type, value, applyToInvoices, applyToQuotes)
                showAddDialog = false
            }
        )
    }
    
    taxToEdit?.let { tax ->
        TaxDialog(
            tax = tax,
            onDismiss = { taxToEdit = null },
            onSave = { name, type, value, applyToInvoices, applyToQuotes ->
                val updatedTax = tax.copy(
                    name = name,
                    type = type,
                    value = value,
                    applyToInvoices = applyToInvoices,
                    applyToQuotes = applyToQuotes
                )
                viewModel.updateTax(updatedTax)
                taxToEdit = null
            }
        )
    }
    
    taxToDelete?.let { tax ->
        AlertDialog(
            onDismissRequest = { taxToDelete = null },
            title = { Text("Supprimer la taxe") },
            text = { Text("Voulez-vous vraiment supprimer la taxe \"${tax.name}\" ?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTax(tax)
                        taxToDelete = null
                    }
                ) {
                    Text("Supprimer")
                }
            },
            dismissButton = {
                TextButton(onClick = { taxToDelete = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun TaxCard(
    tax: Tax,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit
) {
    Card(
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
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = tax.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = when (tax.type) {
                        TaxType.PERCENTAGE -> "${tax.value}%"
                        TaxType.FIXED_AMOUNT -> "${tax.value} TND"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (tax.applyToInvoices) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Factures", style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                    if (tax.applyToQuotes) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Devis", style = MaterialTheme.typography.labelSmall) },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Switch(
                    checked = tax.isActive,
                    onCheckedChange = { onToggleActive() }
                )
                
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier")
                }
                
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxDialog(
    tax: Tax?,
    onDismiss: () -> Unit,
    onSave: (String, TaxType, Double, Boolean, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(tax?.name ?: "") }
    var selectedType by remember { mutableStateOf(tax?.type ?: TaxType.PERCENTAGE) }
    var value by remember { mutableStateOf(tax?.value?.toString() ?: "") }
    var applyToInvoices by remember { mutableStateOf(tax?.applyToInvoices ?: true) }
    var applyToQuotes by remember { mutableStateOf(tax?.applyToQuotes ?: true) }
    var expandedTypeMenu by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (tax == null) "Nouvelle taxe" else "Modifier la taxe") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de la taxe") },
                    placeholder = { Text("Ex: TVA, TPS, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                ExposedDropdownMenuBox(
                    expanded = expandedTypeMenu,
                    onExpandedChange = { expandedTypeMenu = it }
                ) {
                    OutlinedTextField(
                        value = when (selectedType) {
                            TaxType.PERCENTAGE -> "Pourcentage (%)"
                            TaxType.FIXED_AMOUNT -> "Montant fixe"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Type de taxe") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeMenu) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedTypeMenu,
                        onDismissRequest = { expandedTypeMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Pourcentage (%)") },
                            onClick = {
                                selectedType = TaxType.PERCENTAGE
                                expandedTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Montant fixe") },
                            onClick = {
                                selectedType = TaxType.FIXED_AMOUNT
                                expandedTypeMenu = false
                            }
                        )
                    }
                }
                
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("Valeur") },
                    placeholder = { Text(if (selectedType == TaxType.PERCENTAGE) "Ex: 19" else "Ex: 5.50") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    suffix = {
                        Text(if (selectedType == TaxType.PERCENTAGE) "%" else "TND")
                    }
                )
                
                Text(
                    text = "Appliquer à:",
                    style = MaterialTheme.typography.labelLarge
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = applyToInvoices,
                        onClick = { applyToInvoices = !applyToInvoices },
                        label = { Text("Factures") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (applyToInvoices) Icons.Default.Check else Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    
                    FilterChip(
                        selected = applyToQuotes,
                        onClick = { applyToQuotes = !applyToQuotes },
                        label = { Text("Devis") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (applyToQuotes) Icons.Default.Check else Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val valueDouble = value.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && valueDouble > 0) {
                        onSave(name, selectedType, valueDouble, applyToInvoices, applyToQuotes)
                    }
                },
                enabled = name.isNotBlank() && (value.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
