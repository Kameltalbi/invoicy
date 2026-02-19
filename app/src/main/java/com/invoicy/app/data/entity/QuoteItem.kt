package com.invoicy.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entité Ligne de Devis pour la base de données Room
 */
@Entity(
    tableName = "quote_items",
    foreignKeys = [
        ForeignKey(
            entity = Quote::class,
            parentColumns = ["id"],
            childColumns = ["quoteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class QuoteItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quoteId: Long,
    val description: String,
    val quantity: Double,
    val unitPrice: Double,
    val vatRate: Double,
    val position: Int = 0
) {
    /**
     * Calcule le total HT de la ligne
     */
    fun getSubtotal(): Double = quantity * unitPrice

    /**
     * Calcule le montant de TVA de la ligne
     */
    fun getVatAmount(): Double = getSubtotal() * (vatRate / 100.0)

    /**
     * Calcule le total TTC de la ligne
     */
    fun getTotal(): Double = getSubtotal() + getVatAmount()
}
