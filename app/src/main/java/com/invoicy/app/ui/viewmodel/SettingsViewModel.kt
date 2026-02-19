package com.invoicy.app.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invoicy.app.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour les paramètres
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    val companyName: StateFlow<String> = userPreferences.companyName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    
    val email: StateFlow<String> = userPreferences.email
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    
    val phone: StateFlow<String> = userPreferences.phone
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    
    val address: StateFlow<String> = userPreferences.address
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    
    val taxNumber: StateFlow<String> = userPreferences.taxNumber
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    
    val logoUri: StateFlow<String?> = userPreferences.logoUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    val currency: StateFlow<String> = userPreferences.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "EUR")
    
    val defaultFooter: StateFlow<String> = userPreferences.defaultFooter
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    
    val language: StateFlow<String> = userPreferences.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")
    
    val theme: StateFlow<String> = userPreferences.theme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")
    
    val primaryColor: StateFlow<Int> = userPreferences.primaryColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFF6200EE.toInt())
    
    val isPremium: StateFlow<Boolean> = userPreferences.isPremium
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    
    val invoicePrefix: StateFlow<String> = userPreferences.invoicePrefix
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "INV")
    
    val invoiceYearReset: StateFlow<Boolean> = userPreferences.invoiceYearReset
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    val quotePrefix: StateFlow<String> = userPreferences.quotePrefix
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "QUO")
    
    val quoteYearReset: StateFlow<Boolean> = userPreferences.quoteYearReset
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    fun updateCompanyName(value: String) {
        viewModelScope.launch {
            userPreferences.setCompanyName(value)
        }
    }
    
    fun updateEmail(value: String) {
        viewModelScope.launch {
            userPreferences.setEmail(value)
        }
    }
    
    fun updatePhone(value: String) {
        viewModelScope.launch {
            userPreferences.setPhone(value)
        }
    }
    
    fun updateAddress(value: String) {
        viewModelScope.launch {
            userPreferences.setAddress(value)
        }
    }
    
    fun updateTaxNumber(value: String) {
        viewModelScope.launch {
            userPreferences.setTaxNumber(value)
        }
    }
    
    fun updateLogoUri(uri: Uri?) {
        viewModelScope.launch {
            userPreferences.setLogoUri(uri?.toString())
        }
    }
    
    fun updateCurrency(value: String) {
        viewModelScope.launch {
            userPreferences.setCurrency(value)
        }
    }
    
    fun updateDefaultFooter(value: String) {
        viewModelScope.launch {
            userPreferences.setDefaultFooter(value)
        }
    }
    
    fun updateLanguage(value: String) {
        viewModelScope.launch {
            userPreferences.setLanguage(value)
        }
    }
    
    fun updateTheme(value: String) {
        viewModelScope.launch {
            userPreferences.setTheme(value)
        }
    }
    
    fun updatePrimaryColor(value: Int) {
        viewModelScope.launch {
            userPreferences.setPrimaryColor(value)
        }
    }
    
    fun upgradeToPremium() {
        viewModelScope.launch {
            userPreferences.setIsPremium(true)
        }
    }
    
    fun updateInvoiceNumbering(prefix: String, yearReset: Boolean) {
        viewModelScope.launch {
            userPreferences.setInvoicePrefix(prefix)
            userPreferences.setInvoiceYearReset(yearReset)
        }
    }
    
    fun updateQuoteNumbering(prefix: String, yearReset: Boolean) {
        viewModelScope.launch {
            userPreferences.setQuotePrefix(prefix)
            userPreferences.setQuoteYearReset(yearReset)
        }
    }
}
