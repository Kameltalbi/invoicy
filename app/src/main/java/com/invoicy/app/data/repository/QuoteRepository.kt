package com.invoicy.app.data.repository

import com.invoicy.app.data.dao.QuoteDao
import com.invoicy.app.data.dao.QuoteItemDao
import com.invoicy.app.data.entity.*
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
    private val quoteItemDao: QuoteItemDao
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
        val lastNumber = quoteDao.getLastQuoteNumber()
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        
        return if (lastNumber != null && lastNumber.contains(year)) {
            val parts = lastNumber.split("-")
            val number = parts.lastOrNull()?.toIntOrNull() ?: 0
            "DEV-$year-${String.format("%03d", number + 1)}"
        } else {
            "DEV-$year-001"
        }
    }
    
    suspend fun getQuoteCount(): Int = quoteDao.getQuoteCount()
}
