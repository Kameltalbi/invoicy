package com.invoicy.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entité Produit/Service
 */
@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val categoryId: Long,
    val unitPrice: Double = 0.0,
    val vatRate: Double = 20.0,
    val unit: String = "unité", // unité, heure, jour, etc.
    val reference: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
