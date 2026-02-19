package com.invoicy.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entité Facture pour la base de données Room
 */
@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val number: String,
    val clientId: Long,
    val issueDate: Long,
    val dueDate: Long,
    val status: InvoiceStatus,
    val notes: String = "",
    val discount: Double = 0.0,
    val discountType: DiscountType = DiscountType.PERCENTAGE,
    val footer: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val pdfPath: String? = null
)

/**
 * Statut de la facture
 */
enum class InvoiceStatus {
    DRAFT,      // Brouillon
    SENT,       // Envoyée
    PAID,       // Payée
    OVERDUE     // En retard
}

/**
 * Type de remise
 */
enum class DiscountType {
    PERCENTAGE, // Pourcentage
    FIXED       // Montant fixe
}
