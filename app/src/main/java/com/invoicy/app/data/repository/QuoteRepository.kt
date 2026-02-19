package com.invoicy.app.data.repository

import com.invoicy.app.data.dao.QuoteDao
import com.invoicy.app.data.dao.QuoteItemDao
import com.invoicy.app.data.entity.*
import com.invoicy.app.utils.NumberingService
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository pour la gestion des devis
 */
@Singleton
class QuoteRepository @Inject constructor(
    private val quoteDao: QuoteDao,
    private val quoteItemDao: QuoteItemDao,
    private val numberingService: NumberingService
) {
    fun getAllQuotes(): Flow<List<QuoteWithDetails>> = quoteDao.getAllQuotes()
    
    fun getQuoteById(quoteId: Long): Flow<QuoteWithDetails?> = 
        quoteDao.getQuoteById(quoteId)
    
    suspend fun getQuoteByIdSync(quoteId: Long): QuoteWithDetails? = 
        quoteDao.getQuoteByIdSync(quoteId)
    
    fun getQuotesByClient(clientId: Long): Flow<List<QuoteWithDetails>> = 
        quoteDao.getQuotesByClient(clientId)
    
    fun getQuotesByStatus(status: QuoteStatus): Flow<List<QuoteWithDetails>> = 
        quoteDao.getQuotesByStatus(status)
    
    suspend fun insertQuote(quote: Quote, items: List<QuoteItem>): Long {
        val quoteId = quoteDao.insertQuote(quote)
        val itemsWithQuoteId = items.mapIndexed { index, item ->
            item.copy(quoteId = quoteId, position = index)
        }
        quoteItemDao.insertItems(itemsWithQuoteId)
        return quoteId
    }
    
    suspend fun updateQuote(quote: Quote, items: List<QuoteItem>) {
        quoteDao.updateQuote(quote)
        quoteItemDao.deleteItemsByQuote(quote.id)
        val itemsWithPosition = items.mapIndexed { index, item ->
            item.copy(quoteId = quote.id, position = index)
        }
        quoteItemDao.insertItems(itemsWithPosition)
    }
    
    suspend fun deleteQuote(quote: Quote) {
        quoteItemDao.deleteItemsByQuote(quote.id)
        quoteDao.deleteQuote(quote)
    }
    
    suspend fun generateQuoteNumber(): String {
        return numberingService.generateQuoteNumber()
    }
    
    suspend fun duplicateQuote(quoteId: Long): Long {
        val original = getQuoteByIdSync(quoteId) ?: throw Exception("Quote not found")
        val newNumber = generateQuoteNumber()
        
        val newQuote = original.quote.copy(
            id = 0,
            number = newNumber,
            issueDate = System.currentTimeMillis(),
            validUntil = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
            status = QuoteStatus.DRAFT
        )
        
        val newItems = original.items.map { it.copy(id = 0, quoteId = 0) }
        return insertQuote(newQuote, newItems)
    }
    
    suspend fun getQuoteCount(): Int = quoteDao.getQuoteCount()
}
