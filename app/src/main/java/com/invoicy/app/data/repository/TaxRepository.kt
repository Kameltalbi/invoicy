package com.invoicy.app.data.repository

import com.invoicy.app.data.dao.TaxDao
import com.invoicy.app.data.entity.Tax
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository pour gérer les taxes personnalisées
 */
@Singleton
class TaxRepository @Inject constructor(
    private val taxDao: TaxDao
) {
    
    fun getAllTaxes(): Flow<List<Tax>> = taxDao.getAllTaxes()
    
    fun getActiveTaxes(): Flow<List<Tax>> = taxDao.getActiveTaxes()
    
    fun getInvoiceTaxes(): Flow<List<Tax>> = taxDao.getInvoiceTaxes()
    
    fun getQuoteTaxes(): Flow<List<Tax>> = taxDao.getQuoteTaxes()
    
    suspend fun getTaxById(id: Long): Tax? = taxDao.getTaxById(id)
    
    suspend fun insertTax(tax: Tax): Long = taxDao.insertTax(tax)
    
    suspend fun updateTax(tax: Tax) = taxDao.updateTax(tax)
    
    suspend fun deleteTax(tax: Tax) = taxDao.deleteTax(tax)
    
    suspend fun deleteTaxById(id: Long) = taxDao.deleteTaxById(id)
}
