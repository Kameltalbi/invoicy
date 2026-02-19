package com.invoicy.app.ui.navigation

/**
 * Définition des routes de navigation
 */
sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Invoices : Screen("invoices")
    object InvoiceDetail : Screen("invoice/{invoiceId}") {
        fun createRoute(invoiceId: Long) = "invoice/$invoiceId"
    }
    object InvoiceEdit : Screen("invoice/edit/{invoiceId}") {
        fun createRoute(invoiceId: Long) = "invoice/edit/$invoiceId"
    }
    object InvoiceNew : Screen("invoice/new")
    object Quotes : Screen("quotes")
    object QuoteDetail : Screen("quote/{quoteId}") {
        fun createRoute(quoteId: Long) = "quote/$quoteId"
    }
    object QuoteEdit : Screen("quote/edit/{quoteId}") {
        fun createRoute(quoteId: Long) = "quote/edit/$quoteId"
    }
    object QuoteNew : Screen("quote/new")
    object Clients : Screen("clients")
    object ClientDetail : Screen("client/{clientId}") {
        fun createRoute(clientId: Long) = "client/$clientId"
    }
    object ClientEdit : Screen("client/edit/{clientId}") {
        fun createRoute(clientId: Long) = "client/edit/$clientId"
    }
    object ClientNew : Screen("client/new")
    object Settings : Screen("settings")
}
