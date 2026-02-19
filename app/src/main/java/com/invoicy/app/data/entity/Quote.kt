package com.invoicy.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entité Devis pour la base de données Room
 */
@Entity(
    tableName = "quotes",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Quote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val number: String,
    val clientId: Long,
    val issueDate: Long,
    val validUntil: Long,
    val status: QuoteStatus,
    val notes: String = "",
    val discount: Double = 0.0,
    val discountType: DiscountType = DiscountType.PERCENTAGE,
    val footer: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val pdfPath: String? = null
)

/**
 * Statut du devis
 */
enum class QuoteStatus {
    DRAFT,      // Brouillon
    SENT,       // Envoyé
    ACCEPTED,   // Accepté
    REJECTED    // Refusé
}
