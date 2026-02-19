package com.invoicy.app.utils

import com.invoicy.app.data.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service de génération de numéros avancés pour factures et devis
 * Format: PREFIX-YYYY-NNNN (ex: INV-2025-0001)
 */
@Singleton
class NumberingService @Inject constructor(
    private val userPreferences: UserPreferences
) {
    
    /**
     * Génère le prochain numéro de facture
     * Format: PREFIX-YYYY-NNNN ou PREFIX-NNNN selon la configuration
     */
    suspend fun generateInvoiceNumber(): String {
        val prefix = userPreferences.invoicePrefix.first()
        val yearReset = userPreferences.invoiceYearReset.first()
        val lastYear = userPreferences.invoiceLastYear.first()
        val counter = userPreferences.invoiceCounter.first()
        
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        // Vérifier si on doit réinitialiser le compteur
        val newCounter = if (yearReset && lastYear != currentYear) {
            userPreferences.setInvoiceLastYear(currentYear)
            userPreferences.setInvoiceCounter(1)
            1
        } else {
            val next = counter + 1
            userPreferences.setInvoiceCounter(next)
            next
        }
        
        // Formater le numéro
        return if (yearReset) {
            "$prefix-$currentYear-${String.format("%04d", newCounter)}"
        } else {
            "$prefix-${String.format("%04d", newCounter)}"
        }
    }
    
    /**
     * Génère le prochain numéro de devis
     * Format: PREFIX-YYYY-NNNN ou PREFIX-NNNN selon la configuration
     */
    suspend fun generateQuoteNumber(): String {
        val prefix = userPreferences.quotePrefix.first()
        val yearReset = userPreferences.quoteYearReset.first()
        val lastYear = userPreferences.quoteLastYear.first()
        val counter = userPreferences.quoteCounter.first()
        
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        // Vérifier si on doit réinitialiser le compteur
        val newCounter = if (yearReset && lastYear != currentYear) {
            userPreferences.setQuoteLastYear(currentYear)
            userPreferences.setQuoteCounter(1)
            1
        } else {
            val next = counter + 1
            userPreferences.setQuoteCounter(next)
            next
        }
        
        // Formater le numéro
        return if (yearReset) {
            "$prefix-$currentYear-${String.format("%04d", newCounter)}"
        } else {
            "$prefix-${String.format("%04d", newCounter)}"
        }
    }
    
    /**
     * Aperçu du format de numérotation pour les factures
     */
    suspend fun getInvoiceNumberPreview(): String {
        val prefix = userPreferences.invoicePrefix.first()
        val yearReset = userPreferences.invoiceYearReset.first()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        return if (yearReset) {
            "$prefix-$currentYear-0001"
        } else {
            "$prefix-0001"
        }
    }
    
    /**
     * Aperçu du format de numérotation pour les devis
     */
    suspend fun getQuoteNumberPreview(): String {
        val prefix = userPreferences.quotePrefix.first()
        val yearReset = userPreferences.quoteYearReset.first()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        
        return if (yearReset) {
            "$prefix-$currentYear-0001"
        } else {
            "$prefix-0001"
        }
    }
}
