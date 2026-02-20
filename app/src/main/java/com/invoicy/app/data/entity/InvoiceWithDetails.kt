package com.invoicy.app.data.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Classe de relation pour récupérer une facture avec ses lignes et son client
 */
data class InvoiceWithDetails(
    @Embedded val invoice: Invoice,
    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: Client,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val items: List<InvoiceItem>,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val appliedTaxes: List<InvoiceTax> = emptyList()
) {
    /**
     * Calcule le sous-total HT de la facture
     */
    fun getSubtotal(): Double = items.sumOf { it.getSubtotal() }

    /**
     * Calcule le montant total de TVA
     */
    fun getVatTotal(): Double = items.sumOf { it.getVatAmount() }

    /**
     * Calcule le montant de la remise
     */
    fun getDiscountAmount(): Double {
        val subtotal = getSubtotal()
        return when (invoice.discountType) {
            DiscountType.PERCENTAGE -> subtotal * (invoice.discount / 100.0)
            DiscountType.FIXED -> invoice.discount
        }
    }

    /**
     * Calcule le montant total des taxes personnalisées
     */
    fun getCustomTaxesTotal(): Double = appliedTaxes.sumOf { it.taxAmount }
    
    /**
     * Calcule le total TTC de la facture
     */
    fun getTotal(): Double {
        val subtotal = getSubtotal()
        val vatTotal = getVatTotal()
        val discount = getDiscountAmount()
        val customTaxes = getCustomTaxesTotal()
        return subtotal + vatTotal - discount + customTaxes
    }
}
