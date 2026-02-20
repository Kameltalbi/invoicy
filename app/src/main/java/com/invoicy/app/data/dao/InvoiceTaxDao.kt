package com.invoicy.app.data.dao

import androidx.room.*
import com.invoicy.app.data.entity.InvoiceTax
import com.invoicy.app.data.entity.Tax
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les taxes appliquées aux factures
 */
@Dao
interface InvoiceTaxDao {
    
    @Query("SELECT * FROM invoice_taxes WHERE invoiceId = :invoiceId")
    fun getInvoiceTaxes(invoiceId: Long): Flow<List<InvoiceTax>>
    
    @Query("""
        SELECT taxes.* FROM taxes 
        INNER JOIN invoice_taxes ON taxes.id = invoice_taxes.taxId 
        WHERE invoice_taxes.invoiceId = :invoiceId
    """)
    fun getTaxesForInvoice(invoiceId: Long): Flow<List<Tax>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceTax(invoiceTax: InvoiceTax)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceTaxes(invoiceTaxes: List<InvoiceTax>)
    
    @Query("DELETE FROM invoice_taxes WHERE invoiceId = :invoiceId")
    suspend fun deleteInvoiceTaxes(invoiceId: Long)
    
    @Delete
    suspend fun deleteInvoiceTax(invoiceTax: InvoiceTax)
}
