package com.invoicy.app.data.repository

import com.invoicy.app.data.dao.InvoiceDao
import com.invoicy.app.data.dao.InvoiceItemDao
import com.invoicy.app.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository pour la gestion des factures
 */
@Singleton
class InvoiceRepository @Inject constructor(
    private val invoiceDao: InvoiceDao,
    private val invoiceItemDao: InvoiceItemDao
) {
    fun getAllInvoices(): Flow<List<InvoiceWithDetails>> = invoiceDao.getAllInvoices()
    
    fun getInvoiceById(invoiceId: Long): Flow<InvoiceWithDetails?> = 
        invoiceDao.getInvoiceById(invoiceId)
    
    suspend fun getInvoiceByIdSync(invoiceId: Long): InvoiceWithDetails? = 
        invoiceDao.getInvoiceByIdSync(invoiceId)
    
    fun getInvoicesByClient(clientId: Long): Flow<List<InvoiceWithDetails>> = 
        invoiceDao.getInvoicesByClient(clientId)
    
    fun getInvoicesByStatus(status: InvoiceStatus): Flow<List<InvoiceWithDetails>> = 
        invoiceDao.getInvoicesByStatus(status)
    
    suspend fun insertInvoice(invoice: Invoice, items: List<InvoiceItem>): Long {
        val invoiceId = invoiceDao.insertInvoice(invoice)
        val itemsWithInvoiceId = items.mapIndexed { index, item ->
            item.copy(invoiceId = invoiceId, position = index)
        }
        invoiceItemDao.insertItems(itemsWithInvoiceId)
        return invoiceId
    }
    
    suspend fun updateInvoice(invoice: Invoice, items: List<InvoiceItem>) {
        invoiceDao.updateInvoice(invoice)
        invoiceItemDao.deleteItemsByInvoice(invoice.id)
        val itemsWithPosition = items.mapIndexed { index, item ->
            item.copy(invoiceId = invoice.id, position = index)
        }
        invoiceItemDao.insertItems(itemsWithPosition)
    }
    
    suspend fun deleteInvoice(invoice: Invoice) {
        invoiceItemDao.deleteItemsByInvoice(invoice.id)
        invoiceDao.deleteInvoice(invoice)
    }
    
    suspend fun duplicateInvoice(invoiceId: Long): Long? {
        val original = invoiceDao.getInvoiceByIdSync(invoiceId) ?: return null
        val newNumber = generateInvoiceNumber()
        val newInvoice = original.invoice.copy(
            id = 0,
            number = newNumber,
            issueDate = System.currentTimeMillis(),
            status = InvoiceStatus.DRAFT,
            createdAt = System.currentTimeMillis(),
            pdfPath = null
        )
        val newItems = original.items.map { it.copy(id = 0) }
        return insertInvoice(newInvoice, newItems)
    }
    
    suspend fun generateInvoiceNumber(): String {
        val lastNumber = invoiceDao.getLastInvoiceNumber()
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        
        return if (lastNumber != null && lastNumber.contains(year)) {
            val parts = lastNumber.split("-")
            val number = parts.lastOrNull()?.toIntOrNull() ?: 0
            "FAC-$year-${String.format("%03d", number + 1)}"
        } else {
            "FAC-$year-001"
        }
    }
    
    fun getInvoiceCountByStatus(status: InvoiceStatus): Flow<Int> = 
        invoiceDao.getInvoiceCountByStatus(status)
    
    fun getTotalInvoicedThisMonth(): Flow<Double> = invoiceDao.getTotalInvoicedThisMonth()
    
    fun getTotalPaidThisMonth(): Flow<Double> = invoiceDao.getTotalPaidThisMonth()
    
    suspend fun getInvoiceCount(): Int = invoiceDao.getInvoiceCount()
}
