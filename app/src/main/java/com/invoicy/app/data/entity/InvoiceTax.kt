package com.invoicy.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Table de liaison entre factures et taxes
 */
@Entity(
    tableName = "invoice_taxes",
    foreignKeys = [
        ForeignKey(
            entity = Invoice::class,
            parentColumns = ["id"],
            childColumns = ["invoiceId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tax::class,
            parentColumns = ["id"],
            childColumns = ["taxId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("invoiceId"), Index("taxId")]
)
data class InvoiceTax(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val invoiceId: Long,
    
    val taxId: Long,
    
    val taxAmount: Double
)
