package com.invoicy.app.utils

/**
 * Utilitaire pour formater les montants avec la devise appropriée
 */
object CurrencyFormatter {
    
    /**
     * Formate un montant avec la devise
     */
    fun format(amount: Double, currency: String): String {
        return when (currency) {
            "EUR" -> String.format("%.2f €", amount)
            "USD" -> String.format("$%.2f", amount)
            "GBP" -> String.format("£%.2f", amount)
            "TND" -> String.format("%.3f TND", amount)
            "MAD" -> String.format("%.2f MAD", amount)
            "DZD" -> String.format("%.2f DZD", amount)
            else -> String.format("%.2f %s", amount, currency)
        }
    }
    
    /**
     * Obtient le symbole de la devise
     */
    fun getSymbol(currency: String): String {
        return when (currency) {
            "EUR" -> "€"
            "USD" -> "$"
            "GBP" -> "£"
            "TND" -> "TND"
            "MAD" -> "MAD"
            "DZD" -> "DZD"
            else -> currency
        }
    }
}
