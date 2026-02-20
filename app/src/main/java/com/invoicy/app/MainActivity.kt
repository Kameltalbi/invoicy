package com.invoicy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.invoicy.app.data.preferences.UserPreferences
import com.invoicy.app.ui.navigation.InvoicyNavigation
import com.invoicy.app.ui.navigation.MainScreen
import com.invoicy.app.ui.theme.InvoicyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Activité principale de l'application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var userPreferences: UserPreferences
    
    @Inject
    lateinit var dataMigrationHelper: com.invoicy.app.utils.DataMigrationHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Exécuter les migrations pour générer les numéros manquants
        lifecycleScope.launch {
            dataMigrationHelper.runMigrations()
        }
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            val language by userPreferences.language.collectAsState(initial = "en")
            val theme by userPreferences.theme.collectAsState(initial = "system")
            val primaryColor by userPreferences.primaryColor.collectAsState(initial = 0xFF6200EE.toInt())
            
            // Appliquer la langue
            LaunchedEffect(language) {
                val locale = when (language) {
                    "fr" -> Locale.FRENCH
                    "ar" -> Locale("ar")
                    else -> Locale.ENGLISH
                }
                Locale.setDefault(locale)
                val config = resources.configuration
                config.setLocale(locale)
                config.setLayoutDirection(locale)
                resources.updateConfiguration(config, resources.displayMetrics)
            }
            
            val darkTheme = when (theme) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            
            InvoicyTheme(
                darkTheme = darkTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val onboardingCompleted by userPreferences.onboardingCompleted.collectAsState(initial = false)
                    
                    if (onboardingCompleted) {
                        MainScreen()
                    } else {
                        InvoicyNavigation(userPreferences = userPreferences)
                    }
                }
            }
        }
    }
}
