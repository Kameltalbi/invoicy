package com.invoicy.app.data.dao

import androidx.room.*
import com.invoicy.app.data.entity.Category
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les catégories
 */
@Dao
interface CategoryDao {
    
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>
    
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategoryById(categoryId: Long): Flow<Category?>
    
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryByIdSync(categoryId: Long): Category?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long
    
    @Update
    suspend fun updateCategory(category: Category)
    
    @Delete
    suspend fun deleteCategory(category: Category)
    
    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoryCount(): Int
}
