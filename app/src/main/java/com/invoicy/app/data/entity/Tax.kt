package com.invoicy.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité représentant une taxe personnalisée
 */
@Entity(tableName = "taxes")
data class Tax(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    
    val type: TaxType,
    
    val value: Double,
    
    val isActive: Boolean = true,
    
    val applyToInvoices: Boolean = true,
    
    val applyToQuotes: Boolean = true,
    
    val createdAt: Long = System.currentTimeMillis()
)

enum class TaxType {
    PERCENTAGE,
    FIXED_AMOUNT
}
