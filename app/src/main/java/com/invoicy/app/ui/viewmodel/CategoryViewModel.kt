package com.invoicy.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invoicy.app.data.entity.Category
import com.invoicy.app.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun saveCategory(category: Category): Result<Long> {
        return try {
            var id: Long = 0
            viewModelScope.launch {
                id = if (category.id == 0L) {
                    categoryRepository.insertCategory(category)
                } else {
                    categoryRepository.updateCategory(category)
                    category.id
                }
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
}
