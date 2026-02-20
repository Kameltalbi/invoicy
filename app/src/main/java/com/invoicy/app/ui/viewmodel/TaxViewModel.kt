package com.invoicy.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invoicy.app.data.entity.Tax
import com.invoicy.app.data.entity.TaxType
import com.invoicy.app.data.repository.TaxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour gérer les taxes personnalisées
 */
@HiltViewModel
class TaxViewModel @Inject constructor(
    private val taxRepository: TaxRepository
) : ViewModel() {
    
    private val _taxes = MutableStateFlow<List<Tax>>(emptyList())
    val taxes: StateFlow<List<Tax>> = _taxes.asStateFlow()
    
    init {
        loadTaxes()
    }
    
    private fun loadTaxes() {
        viewModelScope.launch {
            taxRepository.getAllTaxes().collect { taxList ->
                _taxes.value = taxList
            }
        }
    }
    
    fun createTax(
        name: String,
        type: TaxType,
        value: Double,
        applyToInvoices: Boolean,
        applyToQuotes: Boolean
    ) {
        viewModelScope.launch {
            val tax = Tax(
                name = name,
                type = type,
                value = value,
                isActive = true,
                applyToInvoices = applyToInvoices,
                applyToQuotes = applyToQuotes
            )
            taxRepository.insertTax(tax)
        }
    }
    
    fun updateTax(tax: Tax) {
        viewModelScope.launch {
            taxRepository.updateTax(tax)
        }
    }
    
    fun deleteTax(tax: Tax) {
        viewModelScope.launch {
            taxRepository.deleteTax(tax)
        }
    }
    
    fun toggleTaxActive(tax: Tax) {
        viewModelScope.launch {
            val updatedTax = tax.copy(isActive = !tax.isActive)
            taxRepository.updateTax(updatedTax)
        }
    }
}
