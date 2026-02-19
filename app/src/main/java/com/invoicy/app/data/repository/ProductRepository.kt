package com.invoicy.app.data.repository

import com.invoicy.app.data.dao.ProductDao
import com.invoicy.app.data.entity.Product
import com.invoicy.app.data.entity.ProductWithCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository pour la gestion des produits/services
 */
@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
    
    fun getAllProducts(): Flow<List<ProductWithCategory>> = productDao.getAllProducts()
    
    fun getAllProductsIncludingInactive(): Flow<List<ProductWithCategory>> = 
        productDao.getAllProductsIncludingInactive()
    
    fun getProductById(productId: Long): Flow<ProductWithCategory?> = 
        productDao.getProductById(productId)
    
    fun getProductsByCategory(categoryId: Long): Flow<List<ProductWithCategory>> = 
        productDao.getProductsByCategory(categoryId)
    
    fun searchProducts(query: String): Flow<List<Product>> = 
        productDao.searchProducts(query)
    
    suspend fun insertProduct(product: Product): Long {
        return productDao.insertProduct(product)
    }
    
    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }
    
    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }
    
    suspend fun getActiveProductCount(): Int = productDao.getActiveProductCount()
    
    suspend fun getProductCountByCategory(categoryId: Long): Int = 
        productDao.getProductCountByCategory(categoryId)
}
