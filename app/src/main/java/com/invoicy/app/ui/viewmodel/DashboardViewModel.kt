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
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
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
    val isLoading: Boolean = true
)
