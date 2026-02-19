package com.invoicy.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité Client pour la base de données Room
 */
@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val country: String,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
