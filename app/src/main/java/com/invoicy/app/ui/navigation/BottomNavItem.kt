package com.invoicy.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Items de la Bottom Navigation
 */
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Dashboard : BottomNavItem(
        route = "dashboard",
        icon = Icons.Default.Dashboard,
        label = "Accueil"
    )
    
    object Invoices : BottomNavItem(
        route = "invoices",
        icon = Icons.Default.ReceiptLong,
        label = "Factures"
    )
    
    object Quotes : BottomNavItem(
        route = "quotes",
        icon = Icons.Default.Description,
        label = "Devis"
    )
    
    object Clients : BottomNavItem(
        route = "clients",
        icon = Icons.Default.People,
        label = "Clients"
    )
    
    object More : BottomNavItem(
        route = "more",
        icon = Icons.Default.MoreHoriz,
        label = "Plus"
    )
    
    companion object {
        val items = listOf(Dashboard, Invoices, Quotes, Clients, More)
    }
}
