package com.invoicy.app.data.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Produit avec sa catégorie
 */
data class ProductWithCategory(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category
)
