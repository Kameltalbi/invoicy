package com.invoicy.app.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.invoicy.app.data.preferences.UserPreferences
import com.invoicy.app.ui.screen.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Gestion de la navigation principale de l'application
 */
@Composable
fun InvoicyNavigation(
    userPreferences: UserPreferences,
    navController: NavHostController = rememberNavController()
) {
    val scope = rememberCoroutineScope()
    var startDestination by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        val onboardingCompleted = userPreferences.onboardingCompleted.first()
        startDestination = if (onboardingCompleted) {
            Screen.Dashboard.route
        } else {
            Screen.Onboarding.route
        }
    }
    
    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!
        ) {
            // Onboarding
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        scope.launch {
                            userPreferences.setOnboardingCompleted(true)
                            navController.navigate(Screen.Dashboard.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    }
                )
            }
            
            // Dashboard
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToInvoices = {
                        navController.navigate(Screen.Invoices.route)
                    },
                    onNavigateToQuotes = {
                        navController.navigate(Screen.Quotes.route)
                    },
                    onNavigateToClients = {
                        navController.navigate(Screen.Clients.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route)
                    }
                )
            }
            
            // Invoices
            composable(Screen.Invoices.route) {
                InvoiceListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToInvoice = { invoiceId ->
                        navController.navigate(Screen.InvoiceDetail.createRoute(invoiceId))
                    },
                    onNavigateToNewInvoice = {
                        navController.navigate(Screen.InvoiceNew.route)
                    }
                )
            }
            
            composable(
                route = Screen.InvoiceDetail.route,
                arguments = listOf(navArgument("invoiceId") { type = NavType.LongType })
            ) { backStackEntry ->
                val invoiceId = backStackEntry.arguments?.getLong("invoiceId") ?: 0L
                InvoiceDetailScreen(
                    invoiceId = invoiceId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.InvoiceEdit.createRoute(id))
                    }
                )
            }
            
            composable(Screen.InvoiceNew.route) {
                InvoiceEditScreen(
                    invoiceId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.InvoiceEdit.route,
                arguments = listOf(navArgument("invoiceId") { type = NavType.LongType })
            ) { backStackEntry ->
                val invoiceId = backStackEntry.arguments?.getLong("invoiceId") ?: 0L
                InvoiceEditScreen(
                    invoiceId = invoiceId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Quotes
            composable(Screen.Quotes.route) {
                QuoteListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToQuote = { quoteId ->
                        navController.navigate(Screen.QuoteDetail.createRoute(quoteId))
                    },
                    onNavigateToNewQuote = {
                        navController.navigate(Screen.QuoteNew.route)
                    }
                )
            }
            
            composable(
                route = Screen.QuoteDetail.route,
                arguments = listOf(navArgument("quoteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val quoteId = backStackEntry.arguments?.getLong("quoteId") ?: 0L
                QuoteEditScreen(
                    quoteId = quoteId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.QuoteNew.route) {
                QuoteEditScreen(
                    quoteId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.QuoteEdit.route,
                arguments = listOf(navArgument("quoteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val quoteId = backStackEntry.arguments?.getLong("quoteId") ?: 0L
                QuoteEditScreen(
                    quoteId = quoteId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Clients
            composable(Screen.Clients.route) {
                ClientListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToClient = { clientId ->
                        navController.navigate(Screen.ClientDetail.createRoute(clientId))
                    },
                    onNavigateToNewClient = {
                        navController.navigate(Screen.ClientNew.route)
                    }
                )
            }
            
            composable(
                route = Screen.ClientDetail.route,
                arguments = listOf(navArgument("clientId") { type = NavType.LongType })
            ) { backStackEntry ->
                val clientId = backStackEntry.arguments?.getLong("clientId") ?: 0L
                ClientEditScreen(
                    clientId = clientId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.ClientNew.route) {
                ClientEditScreen(
                    clientId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.ClientEdit.route,
                arguments = listOf(navArgument("clientId") { type = NavType.LongType })
            ) { backStackEntry ->
                val clientId = backStackEntry.arguments?.getLong("clientId") ?: 0L
                ClientEditScreen(
                    clientId = clientId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Settings
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
