@file:OptIn(ExperimentalMaterial3Api::class)

package com.invoicy.app.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.invoicy.app.ui.screen.*

/**
 * Écran principal avec Bottom Navigation
 */
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Routes où la bottom nav doit être visible
    val bottomNavRoutes = BottomNavItem.items.map { it.route }
    val showBottomBar = currentDestination?.route in bottomNavRoutes
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Dashboard
            composable(BottomNavItem.Dashboard.route) {
                DashboardScreen(
                    onNavigateToInvoices = {
                        navController.navigate(BottomNavItem.Invoices.route)
                    },
                    onNavigateToQuotes = {
                        navController.navigate(BottomNavItem.Quotes.route)
                    },
                    onNavigateToClients = {
                        navController.navigate(BottomNavItem.Clients.route)
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            
            // Invoices
            composable(BottomNavItem.Invoices.route) {
                InvoiceListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToInvoice = { invoiceId ->
                        navController.navigate("invoice_detail/$invoiceId")
                    },
                    onNavigateToNewInvoice = {
                        navController.navigate("invoice_new")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            
            composable(
                route = "invoice_detail/{invoiceId}",
                arguments = listOf(navArgument("invoiceId") { type = NavType.LongType })
            ) { backStackEntry ->
                val invoiceId = backStackEntry.arguments?.getLong("invoiceId") ?: 0L
                InvoiceDetailScreen(
                    invoiceId = invoiceId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate("invoice_edit/$id")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            
            composable("invoice_new") {
                InvoiceEditScreen(
                    invoiceId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = "invoice_edit/{invoiceId}",
                arguments = listOf(navArgument("invoiceId") { type = NavType.LongType })
            ) { backStackEntry ->
                val invoiceId = backStackEntry.arguments?.getLong("invoiceId") ?: 0L
                InvoiceEditScreen(
                    invoiceId = invoiceId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Quotes
            composable(BottomNavItem.Quotes.route) {
                QuoteListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToQuote = { quoteId ->
                        navController.navigate("quote_detail/$quoteId")
                    },
                    onNavigateToNewQuote = {
                        navController.navigate("quote_new")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            
            composable(
                route = "quote_detail/{quoteId}",
                arguments = listOf(navArgument("quoteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val quoteId = backStackEntry.arguments?.getLong("quoteId") ?: 0L
                QuoteDetailScreen(
                    quoteId = quoteId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id ->
                        navController.navigate("quote_edit/$id")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            
            composable("quote_new") {
                QuoteEditScreen(
                    quoteId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = "quote_edit/{quoteId}",
                arguments = listOf(navArgument("quoteId") { type = NavType.LongType })
            ) { backStackEntry ->
                val quoteId = backStackEntry.arguments?.getLong("quoteId") ?: 0L
                QuoteEditScreen(
                    quoteId = quoteId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Clients
            composable(BottomNavItem.Clients.route) {
                ClientListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToClient = { clientId ->
                        navController.navigate("client_detail/$clientId")
                    },
                    onNavigateToNewClient = {
                        navController.navigate("client_new")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }
            
            composable(
                route = "client_detail/{clientId}",
                arguments = listOf(navArgument("clientId") { type = NavType.LongType })
            ) { backStackEntry ->
                val clientId = backStackEntry.arguments?.getLong("clientId") ?: 0L
                ClientEditScreen(
                    clientId = clientId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable("client_new") {
                ClientEditScreen(
                    clientId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = "client_edit/{clientId}",
                arguments = listOf(navArgument("clientId") { type = NavType.LongType })
            ) { backStackEntry ->
                val clientId = backStackEntry.arguments?.getLong("clientId") ?: 0L
                ClientEditScreen(
                    clientId = clientId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // More
            composable(BottomNavItem.More.route) {
                MoreScreen(
                    onNavigateToProducts = {
                        navController.navigate("products")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    },
                    onNavigateToCategoryManagement = {
                        navController.navigate("category_management")
                    }
                )
            }
            
            // Products
            composable("products") {
                ProductListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProduct = { productId ->
                        navController.navigate("product_edit/$productId")
                    },
                    onNavigateToNewProduct = {
                        navController.navigate("product_new")
                    },
                    onNavigateToCategories = {
                        navController.navigate("categories")
                    }
                )
            }
            
            composable("product_new") {
                ProductEditScreen(
                    productId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = "product_edit/{productId}",
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                ProductEditScreen(
                    productId = productId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Categories
            composable("categories") {
                CategoryListScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Settings
            composable("settings") {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToTaxManagement = {
                        navController.navigate("tax_management")
                    },
                    onNavigateToCategoryManagement = {
                        navController.navigate("category_management")
                    }
                )
            }
            
            // Tax Management
            composable("tax_management") {
                TaxManagementScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Category Management
            composable("category_management") {
                CategoryManagementScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
