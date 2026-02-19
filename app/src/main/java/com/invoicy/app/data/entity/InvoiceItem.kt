package com.invoicy.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entité Ligne de Facture pour la base de données Room
 */
@Entity(
    tableName = "invoice_items",
    foreignKeys = [
        ForeignKey(
            entity = Invoice::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class InvoiceItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceId: Long,
    val description: String,
    val quantity: Double,
    val unitPrice: Double,
    val vatRate: Double, // Taux de TVA en pourcentage (ex: 20.0 pour 20%)
    val position: Int = 0 // Pour l'ordre d'affichage
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
