package com.invoicy.app.data.dao

import androidx.room.*
import com.invoicy.app.data.entity.InvoiceItem
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les opérations sur les lignes de facture
 */
@Dao
interface InvoiceItemDao {
    
    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId ORDER BY position ASC")
    fun getItemsByInvoice(invoiceId: Long): Flow<List<InvoiceItem>>
    
    @Query("SELECT * FROM invoice_items WHERE invoiceId = :invoiceId ORDER BY position ASC")
    suspend fun getItemsByInvoiceSync(invoiceId: Long): List<InvoiceItem>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InvoiceItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<InvoiceItem>)
    
    @Update
    suspend fun updateItem(item: InvoiceItem)
    
    @Delete
    suspend fun deleteItem(item: InvoiceItem)
    
    @Query("DELETE FROM invoice_items WHERE invoiceId = :invoiceId")
    suspend fun deleteItemsByInvoice(invoiceId: Long)
}
