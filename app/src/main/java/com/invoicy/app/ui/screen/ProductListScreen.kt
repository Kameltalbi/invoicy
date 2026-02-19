@file:OptIn(ExperimentalMaterial3Api::class)

package com.invoicy.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.invoicy.app.ui.viewmodel.CategoryViewModel
import com.invoicy.app.ui.viewmodel.ProductViewModel

/**
 * Écran de liste des produits/services
 */
@Composable
fun ProductListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProduct: (Long) -> Unit,
    onNavigateToNewProduct: () -> Unit,
    onNavigateToCategories: () -> Unit,
    productViewModel: ProductViewModel = hiltViewModel(),
    categoryViewModel: CategoryViewModel = hiltViewModel()
) {
    val products by productViewModel.products.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    
    val filteredProducts = if (selectedCategoryId != null) {
        products.filter { it.product.categoryId == selectedCategoryId }
    } else {
        products
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Produits & Services") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCategories) {
                        Icon(Icons.Default.Category, contentDescription = "Catégories")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToNewProduct) {
                Icon(Icons.Default.Add, contentDescription = "Nouveau produit")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtres par catégorie
            if (categories.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryId == null,
                            onClick = { selectedCategoryId = null },
                            label = { Text("Tous") }
                        )
                    }
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = { selectedCategoryId = category.id },
                            label = { Text(category.name) },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            Color(category.color),
                                            shape = MaterialTheme.shapes.small
                                        )
                                )
                            }
                        )
                    }
                }
            }
            
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Aucun produit",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Button(onClick = onNavigateToNewProduct) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Ajouter un produit")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProducts) { productWithCategory ->
                        ProductCard(
                            productWithCategory = productWithCategory,
                            onClick = { onNavigateToProduct(productWithCategory.product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    productWithCategory: com.invoicy.app.data.entity.ProductWithCategory,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Indicateur de catégorie
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .background(
                        Color(productWithCategory.category.color),
                        shape = MaterialTheme.shapes.small
                    )
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = productWithCategory.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = productWithCategory.category.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (productWithCategory.product.reference.isNotEmpty()) {
                    Text(
                        text = "Réf: ${productWithCategory.product.reference}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${productWithCategory.product.unitPrice} €",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "/ ${productWithCategory.product.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "TVA ${productWithCategory.product.vatRate}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
