package com.invoicy.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.invoicy.app.data.dao.*
import com.invoicy.app.data.entity.*

/**
 * Base de données Room principale de l'application
 */
@Database(
    entities = [
        Client::class,
        Invoice::class,
        InvoiceItem::class,
        Quote::class,
        QuoteItem::class,
        Category::class,
        Product::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class InvoicyDatabase : RoomDatabase() {
    
    abstract fun clientDao(): ClientDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun invoiceItemDao(): InvoiceItemDao
    abstract fun quoteDao(): QuoteDao
    abstract fun quoteItemDao(): QuoteItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    
    companion object {
        const val DATABASE_NAME = "invoicy_database"
    }
}
