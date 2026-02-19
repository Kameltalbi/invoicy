package com.invoicy.app.data.repository

import com.invoicy.app.data.dao.CategoryDao
import com.invoicy.app.data.entity.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository pour la gestion des catégories
 */
@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()
    
    fun getCategoryById(categoryId: Long): Flow<Category?> = 
        categoryDao.getCategoryById(categoryId)
    
    suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category)
    }
    
    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }
    
    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }
    
    suspend fun getCategoryCount(): Int = categoryDao.getCategoryCount()
}
