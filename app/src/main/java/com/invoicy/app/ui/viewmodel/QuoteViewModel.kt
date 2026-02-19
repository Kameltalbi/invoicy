package com.invoicy.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invoicy.app.data.entity.*
import com.invoicy.app.data.preferences.UserPreferences
import com.invoicy.app.data.repository.InvoiceRepository
import com.invoicy.app.data.repository.QuoteRepository
import com.invoicy.app.utils.PdfGenerator
import com.invoicy.app.utils.ShareHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel pour la gestion des devis
 */
@HiltViewModel
class QuoteViewModel @Inject constructor(
    private val quoteRepository: QuoteRepository,
    private val invoiceRepository: InvoiceRepository,
    private val userPreferences: UserPreferences,
    private val pdfGenerator: PdfGenerator,
    private val shareHelper: ShareHelper
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(QuoteUiState())
    val uiState: StateFlow<QuoteUiState> = _uiState.asStateFlow()
    
    val quotes: StateFlow<List<QuoteWithDetails>> = quoteRepository.getAllQuotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun loadQuote(quoteId: Long) {
        viewModelScope.launch {
            quoteRepository.getQuoteById(quoteId).collect { quote ->
                if (quote != null) {
                    _uiState.update { it.copy(
                        currentQuote = quote,
                        editMode = true
                    )}
                }
            }
        }
    }
    
    fun createNewQuote() {
        viewModelScope.launch {
            val number = quoteRepository.generateQuoteNumber()
            _uiState.update { it.copy(
                currentQuote = null,
                editMode = false,
                generatedNumber = number
            )}
        }
    }
    
    suspend fun saveQuote(
        quote: Quote,
        items: List<QuoteItem>
    ): Result<Long> {
        return try {
            val id = if (quote.id == 0L) {
                quoteRepository.insertQuote(quote, items)
            } else {
                quoteRepository.updateQuote(quote, items)
                quote.id
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun deleteQuote(quote: Quote) {
        viewModelScope.launch {
            quoteRepository.deleteQuote(quote)
        }
    }
    
    suspend fun convertToInvoice(quoteId: Long): Result<Long> {
        return try {
            val quote = quoteRepository.getQuoteByIdSync(quoteId)
                ?: return Result.failure(Exception("Quote not found"))
            
            val invoiceNumber = invoiceRepository.generateInvoiceNumber()
            val invoice = Invoice(
                number = invoiceNumber,
                clientId = quote.quote.clientId,
                issueDate = System.currentTimeMillis(),
                dueDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000), // 30 jours
                status = InvoiceStatus.DRAFT,
                notes = quote.quote.notes,
                discount = quote.quote.discount,
                discountType = quote.quote.discountType,
                footer = quote.quote.footer
            )
            
            val invoiceItems = quote.items.map { quoteItem ->
                InvoiceItem(
                    invoiceId = 0,
                    description = quoteItem.description,
                    quantity = quoteItem.quantity,
                    unitPrice = quoteItem.unitPrice,
                    vatRate = quoteItem.vatRate,
                    position = quoteItem.position
                )
            }
            
            val invoiceId = invoiceRepository.insertInvoice(invoice, invoiceItems)
            
            // Mettre à jour le statut du devis
            val updatedQuote = quote.quote.copy(status = QuoteStatus.ACCEPTED)
            quoteRepository.updateQuote(updatedQuote, quote.items)
            
            Result.success(invoiceId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun generatePdf(quoteId: Long): Result<File> {
        return try {
            val quote = quoteRepository.getQuoteByIdSync(quoteId)
                ?: return Result.failure(Exception("Quote not found"))
            
            val companyName = userPreferences.companyName.first()
            val email = userPreferences.email.first()
            val phone = userPreferences.phone.first()
            val address = userPreferences.address.first()
            val taxNumber = userPreferences.taxNumber.first()
            val logoUri = userPreferences.logoUri.first()
            val currency = userPreferences.currency.first()
            val footer = userPreferences.defaultFooter.first()
            
            val file = pdfGenerator.generateQuotePdf(
                quote, companyName, email, phone, address, taxNumber, logoUri, currency, footer
            )
            
            val updatedQuote = quote.quote.copy(pdfPath = file.absolutePath)
            quoteRepository.updateQuote(updatedQuote, quote.items)
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun sharePdf(file: File) {
        shareHelper.sharePdf(file)
    }
}

/**
 * État UI des devis
 */
data class QuoteUiState(
    val currentQuote: QuoteWithDetails? = null,
    val editMode: Boolean = false,
    val generatedNumber: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
