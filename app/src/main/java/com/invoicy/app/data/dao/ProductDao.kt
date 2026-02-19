package com.invoicy.app.data.dao

import androidx.room.*
import com.invoicy.app.data.entity.Product
import com.invoicy.app.data.entity.ProductWithCategory
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les produits/services
 */
@Dao
interface ProductDao {
    
    @Transaction
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductWithCategory>>
    
    @Transaction
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProductsIncludingInactive(): Flow<List<ProductWithCategory>>
    
    @Transaction
    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Long): Flow<ProductWithCategory?>
    
    @Transaction
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductByIdSync(productId: Long): ProductWithCategory?
    
    @Transaction
    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND isActive = 1 ORDER BY name ASC")
    fun getProductsByCategory(categoryId: Long): Flow<List<ProductWithCategory>>
    
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR reference LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long
    
    @Update
    suspend fun updateProduct(product: Product)
    
    @Delete
    suspend fun deleteProduct(product: Product)
    
    @Query("SELECT COUNT(*) FROM products WHERE isActive = 1")
    suspend fun getActiveProductCount(): Int
    
    @Query("SELECT COUNT(*) FROM products WHERE categoryId = :categoryId")
    suspend fun getProductCountByCategory(categoryId: Long): Int
}
