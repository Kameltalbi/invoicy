package com.invoicy.app.data.dao

import androidx.room.*
import com.invoicy.app.data.entity.Client
import kotlinx.coroutines.flow.Flow

/**
 * DAO pour les opérations sur les clients
 */
@Dao
interface ClientDao {
    
    @Query("SELECT * FROM clients WHERE isArchived = 0 ORDER BY name ASC")
    fun getAllClients(): Flow<List<Client>>
    
    @Query("SELECT * FROM clients WHERE id = :clientId")
    fun getClientById(clientId: Long): Flow<Client?>
    
    @Query("SELECT * FROM clients WHERE id = :clientId")
    suspend fun getClientByIdSync(clientId: Long): Client?
    
    @Query("SELECT * FROM clients WHERE isArchived = 0 AND (name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%')")
    fun searchClients(query: String): Flow<List<Client>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client): Long
    
    @Update
    suspend fun updateClient(client: Client)
    
    @Delete
    suspend fun deleteClient(client: Client)
    
    @Query("UPDATE clients SET isArchived = 1 WHERE id = :clientId")
    suspend fun archiveClient(clientId: Long)
    
    @Query("SELECT COUNT(*) FROM clients WHERE isArchived = 0")
    fun getClientCount(): Flow<Int>
}
