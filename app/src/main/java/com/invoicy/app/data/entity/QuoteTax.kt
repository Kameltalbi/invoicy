package com.invoicy.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table de liaison entre devis et taxes
 */
@Entity(
    tableName = "quote_taxes",
    foreignKeys = [
        ForeignKey(
            entity = Quote::class,
            parentColumns = ["id"],
            childColumns = ["quoteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tax::class,
            parentColumns = ["id"],
            childColumns = ["taxId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("quoteId"), Index("taxId")]
)
data class QuoteTax(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val quoteId: Long,
    
    val taxId: Long,
    
    val taxAmount: Double
)
