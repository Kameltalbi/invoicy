package com.invoicy.app.data.dao

import androidx.room.*
import com.invoicy.app.data.entity.Invoice
import com.invoicy.app.data.entity.InvoiceStatus
import com.invoicy.app.data.entity.InvoiceWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les opérations sur les factures
 */
@Dao
interface InvoiceDao {
    
    @Transaction
    @Query("SELECT * FROM invoices ORDER BY issueDate DESC")
    fun getAllInvoices(): Flow<List<InvoiceWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    fun getInvoiceById(invoiceId: Long): Flow<InvoiceWithDetails?>
    
    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    suspend fun getInvoiceByIdSync(invoiceId: Long): InvoiceWithDetails?
    
    @Transaction
    @Query("SELECT * FROM invoices WHERE clientId = :clientId ORDER BY issueDate DESC")
    fun getInvoicesByClient(clientId: Long): Flow<List<InvoiceWithDetails>>
    
    @Transaction
    @Query("SELECT * FROM invoices WHERE status = :status ORDER BY issueDate DESC")
    fun getInvoicesByStatus(status: InvoiceStatus): Flow<List<InvoiceWithDetails>>
    
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    suspend fun getInvoiceEntityById(invoiceId: Long): Invoice?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long
    
    @Update
    suspend fun updateInvoice(invoice: Invoice)
    
    @Delete
    suspend fun deleteInvoice(invoice: Invoice)
    
    @Query("SELECT COUNT(*) FROM invoices")
    suspend fun getInvoiceCount(): Int
    
    @Query("SELECT COUNT(*) FROM invoices WHERE status = :status")
    fun getInvoiceCountByStatus(status: InvoiceStatus): Flow<Int>
    
    @Query("SELECT number FROM invoices ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastInvoiceNumber(): String?
    
    // Statistiques pour le dashboard
    @Query("""
        SELECT COALESCE(SUM(
            (SELECT COALESCE(SUM(quantity * unitPrice), 0) FROM invoice_items WHERE invoiceId = invoices.id)
            + (SELECT COALESCE(SUM(quantity * unitPrice * vatRate / 100), 0) FROM invoice_items WHERE invoiceId = invoices.id)
            - invoices.discount
        ), 0)
        FROM invoices 
        WHERE strftime('%Y-%m', datetime(issueDate / 1000, 'unixepoch')) = strftime('%Y-%m', 'now')
    """)
    fun getTotalInvoicedThisMonth(): Flow<Double>
    
    @Query("""
        SELECT COALESCE(SUM(
            (SELECT COALESCE(SUM(quantity * unitPrice), 0) FROM invoice_items WHERE invoiceId = invoices.id)
            + (SELECT COALESCE(SUM(quantity * unitPrice * vatRate / 100), 0) FROM invoice_items WHERE invoiceId = invoices.id)
            - invoices.discount
        ), 0)
        FROM invoices 
        WHERE status = 'PAID'
        AND strftime('%Y-%m', datetime(issueDate / 1000, 'unixepoch')) = strftime('%Y-%m', 'now')
    """)
    fun getTotalPaidThisMonth(): Flow<Double>
}
