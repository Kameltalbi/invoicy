package com.invoicy.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invoicy.app.data.entity.*
import com.invoicy.app.data.preferences.UserPreferences
import com.invoicy.app.data.repository.InvoiceRepository
import com.invoicy.app.utils.PdfGenerator
import com.invoicy.app.utils.ShareHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel pour la gestion des factures
 */
@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val userPreferences: UserPreferences,
    private val pdfGenerator: PdfGenerator,
    private val shareHelper: ShareHelper
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(InvoiceUiState())
    val uiState: StateFlow<InvoiceUiState> = _uiState.asStateFlow()
    
    val invoices: StateFlow<List<InvoiceWithDetails>> = invoiceRepository.getAllInvoices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun loadInvoice(invoiceId: Long) {
        viewModelScope.launch {
            invoiceRepository.getInvoiceById(invoiceId).collect { invoice ->
                if (invoice != null) {
                    _uiState.update { it.copy(
                        currentInvoice = invoice,
                        editMode = true
                    )}
                }
            }
        }
    }
    
    fun createNewInvoice() {
        viewModelScope.launch {
            val number = invoiceRepository.generateInvoiceNumber()
            _uiState.update { it.copy(
                currentInvoice = null,
                editMode = false,
                generatedNumber = number
            )}
        }
    }
    
    suspend fun saveInvoice(
        invoice: Invoice,
        items: List<InvoiceItem>
    ): Result<Long> {
        return try {
            val id = if (invoice.id == 0L) {
                invoiceRepository.insertInvoice(invoice, items)
            } else {
                invoiceRepository.updateInvoice(invoice, items)
                invoice.id
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun deleteInvoice(invoice: Invoice) {
        viewModelScope.launch {
            invoiceRepository.deleteInvoice(invoice)
        }
    }
    
    fun duplicateInvoice(invoiceId: Long) {
        viewModelScope.launch {
            invoiceRepository.duplicateInvoice(invoiceId)
        }
    }
    
    suspend fun generatePdf(invoiceId: Long): Result<File> {
        return try {
            val invoice = invoiceRepository.getInvoiceByIdSync(invoiceId)
                ?: return Result.failure(Exception("Invoice not found"))
            
            val companyName = userPreferences.companyName.first()
            val email = userPreferences.email.first()
            val phone = userPreferences.phone.first()
            val address = userPreferences.address.first()
            val taxNumber = userPreferences.taxNumber.first()
            val logoUri = userPreferences.logoUri.first()
            val currency = userPreferences.currency.first()
            val footer = userPreferences.defaultFooter.first()
            
            val file = pdfGenerator.generateInvoicePdf(
                invoice, companyName, email, phone, address, taxNumber, logoUri, currency, footer
            )
            
            // Mettre à jour le chemin du PDF dans la facture
            val updatedInvoice = invoice.invoice.copy(pdfPath = file.absolutePath)
            invoiceRepository.updateInvoice(updatedInvoice, invoice.items)
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun sharePdf(file: File) {
        shareHelper.sharePdf(file)
    }
    
    fun sendPdfByEmail(file: File, invoiceNumber: String) {
        shareHelper.sendPdfByEmail(file, "Facture $invoiceNumber")
    }
    
    fun sendPdfViaWhatsApp(file: File) {
        shareHelper.sendPdfViaWhatsApp(file)
    }
    
    fun savePdfToDownloads(file: File): Boolean {
        return shareHelper.savePdfToDownloads(file)
    }
    
    suspend fun checkInvoiceLimit(): Boolean {
        val isPremium = userPreferences.isPremium.first()
        if (isPremium) return true
        
        val count = invoiceRepository.getInvoiceCount()
        return count < 5
    }
}

/**
 * État UI des factures
 */
data class InvoiceUiState(
    val currentInvoice: InvoiceWithDetails? = null,
    val editMode: Boolean = false,
    val generatedNumber: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
