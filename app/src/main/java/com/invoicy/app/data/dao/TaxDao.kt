package com.invoicy.app.data.dao

import androidx.room.*
import com.invoicy.app.data.entity.Tax
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les taxes personnalisées
 */
@Dao
interface TaxDao {
    
    @Query("SELECT * FROM taxes ORDER BY name ASC")
    fun getAllTaxes(): Flow<List<Tax>>
    
    @Query("SELECT * FROM taxes WHERE isActive = 1 ORDER BY name ASC")
    fun getActiveTaxes(): Flow<List<Tax>>
    
    @Query("SELECT * FROM taxes WHERE isActive = 1 AND applyToInvoices = 1 ORDER BY name ASC")
    fun getInvoiceTaxes(): Flow<List<Tax>>
    
    @Query("SELECT * FROM taxes WHERE isActive = 1 AND applyToQuotes = 1 ORDER BY name ASC")
    fun getQuoteTaxes(): Flow<List<Tax>>
    
    @Query("SELECT * FROM taxes WHERE id = :id")
    suspend fun getTaxById(id: Long): Tax?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTax(tax: Tax): Long
    
    @Update
    suspend fun updateTax(tax: Tax)
    
    @Delete
    suspend fun deleteTax(tax: Tax)
    
    @Query("DELETE FROM taxes WHERE id = :id")
    suspend fun deleteTaxById(id: Long)
}
