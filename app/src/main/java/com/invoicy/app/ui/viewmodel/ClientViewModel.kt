package com.invoicy.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.invoicy.app.data.entity.Client
import com.invoicy.app.data.repository.ClientRepository
import com.invoicy.app.data.repository.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel pour la gestion des clients
 */
@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val invoiceRepository: InvoiceRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    val clients: StateFlow<List<Client>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) {
                clientRepository.getAllClients()
            } else {
                clientRepository.searchClients(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    suspend fun saveClient(client: Client): Result<Long> {
        return try {
            val id = if (client.id == 0L) {
                clientRepository.insertClient(client)
            } else {
                clientRepository.updateClient(client)
                client.id
            }
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun deleteClient(client: Client) {
        viewModelScope.launch {
            clientRepository.deleteClient(client)
        }
    }
    
    fun archiveClient(clientId: Long) {
        viewModelScope.launch {
            clientRepository.archiveClient(clientId)
        }
    }
    
    fun getClientById(clientId: Long): Flow<Client?> {
        return clientRepository.getClientById(clientId)
    }
    
    fun getClientInvoices(clientId: Long) = invoiceRepository.getInvoicesByClient(clientId)
}
