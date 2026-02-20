package com.invoicy.app.data.dao

import androidx.room.*
import com.invoicy.app.data.entity.Quote
import com.invoicy.app.data.entity.QuoteStatus
import com.invoicy.app.data.entity.QuoteWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les opérations sur les devis
 */
@Dao
interface QuoteDao {
    
    @Transaction
    @Query("SELECT * FROM quotes WHERE convertedToInvoiceId IS NULL ORDER BY issueDate DESC")
    fun getAllQuotes(): Flow<List<QuoteWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM quotes WHERE id = :quoteId")
    fun getQuoteById(quoteId: Long): Flow<QuoteWithDetails?>
    
    @Transaction
    @Query("SELECT * FROM quotes WHERE id = :quoteId")
    suspend fun getQuoteByIdSync(quoteId: Long): QuoteWithDetails?
    
    @Transaction
    @Query("SELECT * FROM quotes WHERE clientId = :clientId ORDER BY issueDate DESC")
    fun getQuotesByClient(clientId: Long): Flow<List<QuoteWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM quotes WHERE status = :status ORDER BY issueDate DESC")
    fun getQuotesByStatus(status: QuoteStatus): Flow<List<QuoteWithDetails>>
    
    @Query("SELECT * FROM quotes WHERE id = :quoteId")
    suspend fun getQuoteEntityById(quoteId: Long): Quote?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: Quote): Long
    
    @Update
    suspend fun updateQuote(quote: Quote)
    
    @Delete
    suspend fun deleteQuote(quote: Quote)
    
    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun getQuoteCount(): Int
    
    @Query("SELECT number FROM quotes ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastQuoteNumber(): String?
}
