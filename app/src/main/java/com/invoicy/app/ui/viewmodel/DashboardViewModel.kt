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
        // TODO: Récupérer les vraies données depuis la base
        val months = listOf("Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Aoû", "Sep", "Oct", "Nov", "Déc")
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
        return months.take(currentMonth + 1).mapIndexed { index, month ->
            month to (Math.random() * 5000 + 1000)
        }
    }
    
    private suspend fun generateCategorySalesData(): List<Pair<String, Double>> {
        // TODO: Récupérer les vraies données depuis la base
        return listOf(
            "Consulting" to 12500.0,
            "Développement" to 8900.0,
            "Design" to 5600.0,
            "Formation" to 3200.0,
            "Support" to 2100.0
        )
    }
    
    private suspend fun generateCurrentYearData(): List<Pair<String, Double>> {
        // TODO: Récupérer les vraies données depuis la base
        val months = listOf("Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Aoû", "Sep", "Oct", "Nov", "Déc")
        return months.mapIndexed { index, month ->
            month to (Math.random() * 5000 + 2000)
        }
    }
    
    private suspend fun generatePreviousYearData(): List<Pair<String, Double>> {
        // TODO: Récupérer les vraies données depuis la base
        val months = listOf("Jan", "Fév", "Mar", "Avr", "Mai", "Juin", "Juil", "Aoû", "Sep", "Oct", "Nov", "Déc")
        return months.mapIndexed { index, month ->
            month to (Math.random() * 4000 + 1500)
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
