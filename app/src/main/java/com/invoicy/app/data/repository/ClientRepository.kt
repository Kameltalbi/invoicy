package com.invoicy.app.data.repository

import com.invoicy.app.data.dao.ClientDao
import com.invoicy.app.data.entity.Client
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository pour la gestion des clients
 */
@Singleton
class ClientRepository @Inject constructor(
    private val clientDao: ClientDao
) {
    fun getAllClients(): Flow<List<Client>> = clientDao.getAllClients()
    
    fun getClientById(clientId: Long): Flow<Client?> = clientDao.getClientById(clientId)
    
    suspend fun getClientByIdSync(clientId: Long): Client? = clientDao.getClientByIdSync(clientId)
    
    fun searchClients(query: String): Flow<List<Client>> = clientDao.searchClients(query)
    
    suspend fun insertClient(client: Client): Long = clientDao.insertClient(client)
    
    suspend fun updateClient(client: Client) = clientDao.updateClient(client)
    
    suspend fun deleteClient(client: Client) = clientDao.deleteClient(client)
    
    suspend fun archiveClient(clientId: Long) = clientDao.archiveClient(clientId)
    
    fun getClientCount(): Flow<Int> = clientDao.getClientCount()
}
