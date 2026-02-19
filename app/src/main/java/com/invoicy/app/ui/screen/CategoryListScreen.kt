@file:OptIn(ExperimentalMaterial3Api::class)

package com.invoicy.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.invoicy.app.data.entity.Category
import com.invoicy.app.ui.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch

@Composable
fun CategoryListScreen(
    onNavigateBack: () -> Unit,
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val categories by categoryViewModel.categories.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<Category?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catégories") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Nouvelle catégorie")
            }
        }
    ) { paddingValues ->
        if (categories.isEmpty()) {
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
                        imageVector = Icons.Default.Category,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Aucune catégorie",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ajouter une catégorie")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onEdit = { editingCategory = category },
                        onDelete = {
                            scope.launch {
                                categoryViewModel.deleteCategory(category)
                            }
                        }
                    )
                }
            }
        }
    }
    
    if (showAddDialog) {
        CategoryDialog(
            category = null,
            onDismiss = { showAddDialog = false },
            onSave = { category ->
                scope.launch {
                    categoryViewModel.saveCategory(category)
                    showAddDialog = false
                }
            }
        )
    }
    
    editingCategory?.let { category ->
        CategoryDialog(
            category = category,
            onDismiss = { editingCategory = null },
            onSave = { updatedCategory ->
                scope.launch {
                    categoryViewModel.saveCategory(updatedCategory)
                    editingCategory = null
                }
            }
        )
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(category.color), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (category.description.isNotEmpty()) {
                    Text(
                        text = category.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier")
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun CategoryDialog(
    category: Category?,
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var description by remember { mutableStateOf(category?.description ?: "") }
    var selectedColor by remember { mutableStateOf(category?.color ?: 0xFF6200EE.toInt()) }
    
    val colors = listOf(
        0xFF6200EE.toInt(), 0xFFE91E63.toInt(), 0xFFF44336.toInt(),
        0xFFFF5722.toInt(), 0xFFFF9800.toInt(), 0xFF4CAF50.toInt(),
        0xFF009688.toInt(), 0xFF2196F3.toInt(), 0xFF9C27B0.toInt()
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category == null) "Nouvelle catégorie" else "Modifier catégorie") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom *") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text("Couleur", style = MaterialTheme.typography.labelMedium)
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(color), shape = CircleShape)
                                .then(
                                    if (selectedColor == color) {
                                        Modifier.padding(4.dp)
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(onClick = { selectedColor = color }) {
                                if (selectedColor == color) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(
                            Category(
                                id = category?.id ?: 0,
                                name = name,
                                description = description,
                                color = selectedColor
                            )
                        )
                    }
                }
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
