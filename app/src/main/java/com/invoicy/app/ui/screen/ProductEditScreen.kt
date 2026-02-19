@file:OptIn(ExperimentalMaterial3Api::class)

package com.invoicy.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.invoicy.app.data.entity.Product
import com.invoicy.app.ui.viewmodel.CategoryViewModel
import com.invoicy.app.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductEditScreen(
    productId: Long? = null,
    onNavigateBack: () -> Unit,
    productViewModel: ProductViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val categories by categoryViewModel.categories.collectAsState()
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    var vatRate by remember { mutableStateOf("20") }
    var unit by remember { mutableStateOf("unité") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showUnitDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    
    val units = listOf("unité", "heure", "jour", "mois", "kg", "m", "m²", "m³", "litre")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == null) "Nouveau produit" else "Modifier produit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (name.isBlank()) {
                                    snackbarMessage = "Le nom est obligatoire"
                                    return@launch
                                }
                                if (selectedCategoryId == null) {
                                    snackbarMessage = "Veuillez sélectionner une catégorie"
                                    return@launch
                                }
                                
                                val product = Product(
                                    id = productId ?: 0,
                                    name = name,
                                    description = description,
                                    reference = reference,
                                    categoryId = selectedCategoryId!!,
                                    unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
                                    vatRate = vatRate.toDoubleOrNull() ?: 20.0,
                                    unit = unit
                                )
                                
                                val result = productViewModel.saveProduct(product)
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
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du produit/service *") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Inventory, contentDescription = null)
                    }
                )
            }
            
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
            
            item {
                OutlinedTextField(
                    value = reference,
                    onValueChange = { reference = it },
                    label = { Text("Référence") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(Icons.Default.Tag, contentDescription = null)
                    }
                )
            }
            
            item {
                OutlinedCard(
                    onClick = { showCategoryDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Category, contentDescription = null)
                            Column {
                                Text(
                                    text = "Catégorie *",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = categories.find { it.id == selectedCategoryId }?.name
                                        ?: "Sélectionner une catégorie",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
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
                        value = unitPrice,
                        onValueChange = { unitPrice = it },
                        label = { Text("Prix unitaire *") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = {
                            Icon(Icons.Default.Euro, contentDescription = null)
                        }
                    )
                    
                    OutlinedCard(
                        onClick = { showUnitDialog = true },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Unité",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = unit,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    }
                }
            }
            
            item {
                OutlinedTextField(
                    value = vatRate,
                    onValueChange = { vatRate = it },
                    label = { Text("Taux TVA (%)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    leadingIcon = {
                        Icon(Icons.Default.Percent, contentDescription = null)
                    }
                )
            }
        }
    }
    
    if (showCategoryDialog && categories.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Sélectionner une catégorie") },
            text = {
                LazyColumn {
                    items(categories.size) { index ->
                        val category = categories[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedCategoryId == category.id,
                                onClick = {
                                    selectedCategoryId = category.id
                                    showCategoryDialog = false
                                }
                            )
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(
                                        Color(category.color),
                                        shape = MaterialTheme.shapes.small
                                    )
                            )
                            Text(category.name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
    
    if (showUnitDialog) {
        AlertDialog(
            onDismissRequest = { showUnitDialog = false },
            title = { Text("Sélectionner une unité") },
            text = {
                LazyColumn {
                    items(units.size) { index ->
                        val currentUnit = units[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = unit == currentUnit,
                                onClick = {
                                    unit = currentUnit
                                    showUnitDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(currentUnit)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUnitDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
