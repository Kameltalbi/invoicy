package com.invoicy.app.data.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Classe de relation pour récupérer un devis avec ses lignes et son client
 */
data class QuoteWithDetails(
    @Embedded val quote: Quote,
    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: Client,
    @Relation(
        parentColumn = "id",
        entityColumn = "quoteId"
    )
    val items: List<QuoteItem>,
    @Relation(
        parentColumn = "id",
        entityColumn = "quoteId"
    )
    val appliedTaxes: List<QuoteTax> = emptyList()
) {
    /**
     * Calcule le sous-total HT du devis
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
        return when (quote.discountType) {
            DiscountType.PERCENTAGE -> subtotal * (quote.discount / 100.0)
            DiscountType.FIXED -> quote.discount
        }
    }

    /**
     * Calcule le montant total des taxes personnalisées
     */
    fun getCustomTaxesTotal(): Double = appliedTaxes.sumOf { it.taxAmount }

    /**
     * Calcule le total TTC du devis
     */
    fun getTotal(): Double {
        val subtotal = getSubtotal()
        val vatTotal = getVatTotal()
        val discount = getDiscountAmount()
        val customTaxes = getCustomTaxesTotal()
        return subtotal + vatTotal - discount + customTaxes
    }
}
