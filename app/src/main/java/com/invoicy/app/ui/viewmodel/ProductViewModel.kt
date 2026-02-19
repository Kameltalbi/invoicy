package com.invoicy.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invoicy.app.data.entity.Product
import com.invoicy.app.data.entity.ProductWithCategory
import com.invoicy.app.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {
    
    val products: StateFlow<List<ProductWithCategory>> = productRepository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun getProductsByCategory(categoryId: Long): Flow<List<ProductWithCategory>> =
        productRepository.getProductsByCategory(categoryId)
    
    fun saveProduct(product: Product): Result<Long> {
        return try {
            var id: Long = 0
            viewModelScope.launch {
                id = if (product.id == 0L) {
                    productRepository.insertProduct(product)
                } else {
                    productRepository.updateProduct(product)
                    product.id
                }
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }
}
