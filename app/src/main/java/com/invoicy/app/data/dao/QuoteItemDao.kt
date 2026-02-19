package com.invoicy.app.data.dao

import androidx.room.*
import com.invoicy.app.data.entity.QuoteItem
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les opérations sur les lignes de devis
 */
@Dao
interface QuoteItemDao {
    
    @Query("SELECT * FROM quote_items WHERE quoteId = :quoteId ORDER BY position ASC")
    fun getItemsByQuote(quoteId: Long): Flow<List<QuoteItem>>
    
    @Query("SELECT * FROM quote_items WHERE quoteId = :quoteId ORDER BY position ASC")
    suspend fun getItemsByQuoteSync(quoteId: Long): List<QuoteItem>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: QuoteItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<QuoteItem>)
    
    @Update
    suspend fun updateItem(item: QuoteItem)
    
    @Delete
    suspend fun deleteItem(item: QuoteItem)
    
    @Query("DELETE FROM quote_items WHERE quoteId = :quoteId")
    suspend fun deleteItemsByQuote(quoteId: Long)
}
