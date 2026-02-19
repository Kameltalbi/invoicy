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
}
