package com.invoicy.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestion des préférences utilisateur avec DataStore
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    
    companion object {
        private val COMPANY_NAME = stringPreferencesKey("company_name")
        private val EMAIL = stringPreferencesKey("email")
        private val PHONE = stringPreferencesKey("phone")
        private val ADDRESS = stringPreferencesKey("address")
        private val TAX_NUMBER = stringPreferencesKey("tax_number")
        private val LOGO_URI = stringPreferencesKey("logo_uri")
        private val CURRENCY = stringPreferencesKey("currency")
        private val DEFAULT_FOOTER = stringPreferencesKey("default_footer")
        private val LANGUAGE = stringPreferencesKey("language")
        private val THEME = stringPreferencesKey("theme")
        private val PRIMARY_COLOR = intPreferencesKey("primary_color")
        private val IS_PREMIUM = booleanPreferencesKey("is_premium")
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        
        // Numérotation
        private val INVOICE_PREFIX = stringPreferencesKey("invoice_prefix")
        private val INVOICE_YEAR_RESET = booleanPreferencesKey("invoice_year_reset")
        private val INVOICE_LAST_YEAR = intPreferencesKey("invoice_last_year")
        private val INVOICE_COUNTER = intPreferencesKey("invoice_counter")
        private val QUOTE_PREFIX = stringPreferencesKey("quote_prefix")
        private val QUOTE_YEAR_RESET = booleanPreferencesKey("quote_year_reset")
        private val QUOTE_LAST_YEAR = intPreferencesKey("quote_last_year")
        private val QUOTE_COUNTER = intPreferencesKey("quote_counter")
        
        // Template PDF
        private val PDF_TEMPLATE = stringPreferencesKey("pdf_template")
    }
    
    // Profil émetteur
    val companyName: Flow<String> = dataStore.data.map { it[COMPANY_NAME] ?: "" }
    val email: Flow<String> = dataStore.data.map { it[EMAIL] ?: "" }
    val phone: Flow<String> = dataStore.data.map { it[PHONE] ?: "" }
    val address: Flow<String> = dataStore.data.map { it[ADDRESS] ?: "" }
    val taxNumber: Flow<String> = dataStore.data.map { it[TAX_NUMBER] ?: "" }
    val logoUri: Flow<String?> = dataStore.data.map { it[LOGO_URI] }
    val currency: Flow<String> = dataStore.data.map { it[CURRENCY] ?: "EUR" }
    val defaultFooter: Flow<String> = dataStore.data.map { it[DEFAULT_FOOTER] ?: "" }
    
    // Paramètres app
    val language: Flow<String> = dataStore.data.map { it[LANGUAGE] ?: "en" }
    val theme: Flow<String> = dataStore.data.map { it[THEME] ?: "system" }
    val primaryColor: Flow<Int> = dataStore.data.map { it[PRIMARY_COLOR] ?: 0xFF6200EE.toInt() }
    val isPremium: Flow<Boolean> = dataStore.data.map { it[IS_PREMIUM] ?: false }
    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }
    
    // Numérotation
    val invoicePrefix: Flow<String> = dataStore.data.map { it[INVOICE_PREFIX] ?: "INV" }
    val invoiceYearReset: Flow<Boolean> = dataStore.data.map { it[INVOICE_YEAR_RESET] ?: true }
    val invoiceLastYear: Flow<Int> = dataStore.data.map { it[INVOICE_LAST_YEAR] ?: 0 }
    val invoiceCounter: Flow<Int> = dataStore.data.map { it[INVOICE_COUNTER] ?: 0 }
    val quotePrefix: Flow<String> = dataStore.data.map { it[QUOTE_PREFIX] ?: "QUO" }
    val quoteYearReset: Flow<Boolean> = dataStore.data.map { it[QUOTE_YEAR_RESET] ?: true }
    val quoteLastYear: Flow<Int> = dataStore.data.map { it[QUOTE_LAST_YEAR] ?: 0 }
    val quoteCounter: Flow<Int> = dataStore.data.map { it[QUOTE_COUNTER] ?: 0 }
    
    suspend fun setCompanyName(value: String) {
        dataStore.edit { it[COMPANY_NAME] = value }
    }
    
    suspend fun setEmail(value: String) {
        dataStore.edit { it[EMAIL] = value }
    }
    
    suspend fun setPhone(value: String) {
        dataStore.edit { it[PHONE] = value }
    }
    
    suspend fun setAddress(value: String) {
        dataStore.edit { it[ADDRESS] = value }
    }
    
    suspend fun setTaxNumber(value: String) {
        dataStore.edit { it[TAX_NUMBER] = value }
    }
    
    suspend fun setLogoUri(value: String?) {
        dataStore.edit { 
            if (value != null) {
                it[LOGO_URI] = value
            } else {
                it.remove(LOGO_URI)
            }
        }
    }
    
    suspend fun setCurrency(value: String) {
        dataStore.edit { it[CURRENCY] = value }
    }
    
    suspend fun setDefaultFooter(value: String) {
        dataStore.edit { it[DEFAULT_FOOTER] = value }
    }
    
    suspend fun setLanguage(value: String) {
        dataStore.edit { it[LANGUAGE] = value }
    }
    
    suspend fun setTheme(value: String) {
        dataStore.edit { it[THEME] = value }
    }
    
    suspend fun setPrimaryColor(value: Int) {
        dataStore.edit { it[PRIMARY_COLOR] = value }
    }
    
    suspend fun setIsPremium(value: Boolean) {
        dataStore.edit { it[IS_PREMIUM] = value }
    }
    
    suspend fun setOnboardingCompleted(value: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETED] = value }
    }
    
    // Numérotation
    suspend fun setInvoicePrefix(value: String) {
        dataStore.edit { it[INVOICE_PREFIX] = value }
    }
    
    suspend fun setInvoiceYearReset(value: Boolean) {
        dataStore.edit { it[INVOICE_YEAR_RESET] = value }
    }
    
    suspend fun setInvoiceLastYear(value: Int) {
        dataStore.edit { it[INVOICE_LAST_YEAR] = value }
    }
    
    suspend fun setInvoiceCounter(value: Int) {
        dataStore.edit { it[INVOICE_COUNTER] = value }
    }
    
    suspend fun setQuotePrefix(value: String) {
        dataStore.edit { it[QUOTE_PREFIX] = value }
    }
    
    suspend fun setQuoteYearReset(value: Boolean) {
        dataStore.edit { it[QUOTE_YEAR_RESET] = value }
    }
    
    suspend fun setQuoteLastYear(value: Int) {
        dataStore.edit { it[QUOTE_LAST_YEAR] = value }
    }
    
    suspend fun setQuoteCounter(value: Int) {
        dataStore.edit { it[QUOTE_COUNTER] = value }
    }
}
