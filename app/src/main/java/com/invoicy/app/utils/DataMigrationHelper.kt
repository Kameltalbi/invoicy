package com.invoicy.app.utils

import com.invoicy.app.data.repository.InvoiceRepository
import com.invoicy.app.data.repository.QuoteRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper pour migrer les données existantes
 * Génère des numéros pour les factures et devis qui n'en ont pas
 */
@Singleton
class DataMigrationHelper @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val quoteRepository: QuoteRepository,
    private val numberingService: NumberingService
) {
    
    /**
     * Génère des numéros pour toutes les factures qui n'en ont pas
     */
    suspend fun generateMissingInvoiceNumbers() {
        val invoices = invoiceRepository.getAllInvoices().first()
        
        invoices.forEach { invoiceWithDetails ->
            val invoice = invoiceWithDetails.invoice
            
            // Si le numéro est vide ou null, générer un nouveau numéro
            if (invoice.number.isBlank()) {
                val newNumber = numberingService.generateInvoiceNumber()
                val updatedInvoice = invoice.copy(number = newNumber)
                invoiceRepository.updateInvoice(updatedInvoice, invoiceWithDetails.items)
            }
        }
    }
    
    /**
     * Génère des numéros pour tous les devis qui n'en ont pas
     */
    suspend fun generateMissingQuoteNumbers() {
        val quotes = quoteRepository.getAllQuotes().first()
        
        quotes.forEach { quoteWithDetails ->
            val quote = quoteWithDetails.quote
            
            // Si le numéro est vide ou null, générer un nouveau numéro
            if (quote.number.isBlank()) {
                val newNumber = numberingService.generateQuoteNumber()
                val updatedQuote = quote.copy(number = newNumber)
                quoteRepository.updateQuote(updatedQuote, quoteWithDetails.items)
            }
        }
    }
    
    /**
     * Exécute toutes les migrations nécessaires
     */
    suspend fun runMigrations() {
        generateMissingInvoiceNumbers()
        generateMissingQuoteNumbers()
    }
}
