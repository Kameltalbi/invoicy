package com.invoicy.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité Catégorie pour les produits/services
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val color: Int = 0xFF6200EE.toInt(),
    val createdAt: Long = System.currentTimeMillis()
)
