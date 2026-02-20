package com.invoicy.app.data.dao

import androidx.room.*
import com.invoicy.app.data.entity.QuoteTax
import com.invoicy.app.data.entity.Tax
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les taxes appliquées aux devis
 */
@Dao
interface QuoteTaxDao {
    
    @Query("SELECT * FROM quote_taxes WHERE quoteId = :quoteId")
    fun getQuoteTaxes(quoteId: Long): Flow<List<QuoteTax>>
    
    @Query("""
        SELECT taxes.* FROM taxes 
        INNER JOIN quote_taxes ON taxes.id = quote_taxes.taxId 
        WHERE quote_taxes.quoteId = :quoteId
    """)
    fun getTaxesForQuote(quoteId: Long): Flow<List<Tax>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteTax(quoteTax: QuoteTax)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteTaxes(quoteTaxes: List<QuoteTax>)
    
    @Query("DELETE FROM quote_taxes WHERE quoteId = :quoteId")
    suspend fun deleteQuoteTaxes(quoteId: Long)
    
    @Delete
    suspend fun deleteQuoteTax(quoteTax: QuoteTax)
}
