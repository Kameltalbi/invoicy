package com.invoicy.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invoicy.app.data.entity.InvoiceStatus
import com.invoicy.app.data.repository.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour le tableau de bord
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboardData()
    }
    
    private fun loadDashboardData() {
        viewModelScope.launch {
            combine(
                invoiceRepository.getTotalInvoicedThisMonth(),
                invoiceRepository.getTotalPaidThisMonth(),
                invoiceRepository.getInvoiceCountByStatus(InvoiceStatus.SENT),
                invoiceRepository.getInvoiceCountByStatus(InvoiceStatus.OVERDUE)
            ) { totalInvoiced, totalPaid, pending, overdue ->
                DashboardUiState(
                    totalInvoicedThisMonth = totalInvoiced,
                    totalPaidThisMonth = totalPaid,
                    pendingInvoices = pending,
                    overdueInvoices = overdue,
                    monthlySales = generateMonthlySalesData(),
                    salesByCategory = generateCategorySalesData(),
                    currentYearSales = generateCurrentYearData(),
                    previousYearSales = generatePreviousYearData(),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    private suspend fun generateMonthlySalesData(): List<Pair<String, Double>> {
        val monthsMap = mapOf(
            "01" to "Jan", "02" to "Fév", "03" to "Mar", "04" to "Avr",
            "05" to "Mai", "06" to "Juin", "07" to "Juil", "08" to "Aoû",
            "09" to "Sep", "10" to "Oct", "11" to "Nov", "12" to "Déc"
        )
        
        val salesData = invoiceRepository.getMonthlySalesCurrentYear()
        val salesMap = salesData.associate { it.month to it.total }
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
        
        return (1..currentMonth + 1).map { month ->
            val monthKey = String.format("%02d", month)
            val monthName = monthsMap[monthKey] ?: ""
            val yearMonth = "${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)}-$monthKey"
            monthName to (salesMap[yearMonth] ?: 0.0)
        }
    }
    
    private suspend fun generateCategorySalesData(): List<Pair<String, Double>> {
        // Données de catégories - pour l'instant on garde les données de démo
        // car il faudrait ajouter des catégories aux lignes de facture
        return listOf(
            "Services" to 12500.0,
            "Produits" to 8900.0,
            "Consulting" to 5600.0,
            "Formation" to 3200.0,
            "Support" to 2100.0
        )
    }
    
    private suspend fun generateCurrentYearData(): List<Pair<String, Double>> {
        val monthsMap = mapOf(
            "01" to "Jan", "02" to "Fév", "03" to "Mar", "04" to "Avr",
            "05" to "Mai", "06" to "Juin", "07" to "Juil", "08" to "Aoû",
            "09" to "Sep", "10" to "Oct", "11" to "Nov", "12" to "Déc"
        )
        
        val salesData = invoiceRepository.getMonthlySalesCurrentYear()
        val salesMap = salesData.associate { it.month to it.total }
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        
        return (1..12).map { month ->
            val monthKey = String.format("%02d", month)
            val monthName = monthsMap[monthKey] ?: ""
            val yearMonth = "$currentYear-$monthKey"
            monthName to (salesMap[yearMonth] ?: 0.0)
        }
    }
    
    private suspend fun generatePreviousYearData(): List<Pair<String, Double>> {
        val monthsMap = mapOf(
            "01" to "Jan", "02" to "Fév", "03" to "Mar", "04" to "Avr",
            "05" to "Mai", "06" to "Juin", "07" to "Juil", "08" to "Aoû",
            "09" to "Sep", "10" to "Oct", "11" to "Nov", "12" to "Déc"
        )
        
        val salesData = invoiceRepository.getMonthlySalesPreviousYear()
        val salesMap = salesData.associate { it.month to it.total }
        val previousYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) - 1
        
        return (1..12).map { month ->
            val monthKey = String.format("%02d", month)
            val monthName = monthsMap[monthKey] ?: ""
            val yearMonth = "$previousYear-$monthKey"
            monthName to (salesMap[yearMonth] ?: 0.0)
        }
    }
}

/**
 * État UI du dashboard
 */
data class DashboardUiState(
    val totalInvoicedThisMonth: Double = 0.0,
    val totalPaidThisMonth: Double = 0.0,
    val pendingInvoices: Int = 0,
    val overdueInvoices: Int = 0,
    val monthlySales: List<Pair<String, Double>> = emptyList(),
    val salesByCategory: List<Pair<String, Double>> = emptyList(),
    val currentYearSales: List<Pair<String, Double>> = emptyList(),
    val previousYearSales: List<Pair<String, Double>> = emptyList(),
    val isLoading: Boolean = true
)
