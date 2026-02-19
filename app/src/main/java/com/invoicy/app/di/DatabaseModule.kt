package com.invoicy.app.di

import android.content.Context
import androidx.room.Room
import com.invoicy.app.data.dao.*
import com.invoicy.app.data.database.InvoicyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt pour l'injection de dépendances de la base de données
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): InvoicyDatabase {
        return Room.databaseBuilder(
            context,
            InvoicyDatabase::class.java,
            InvoicyDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideClientDao(database: InvoicyDatabase): ClientDao {
        return database.clientDao()
    }
    
    @Provides
    fun provideInvoiceDao(database: InvoicyDatabase): InvoiceDao {
        return database.invoiceDao()
    }
    
    @Provides
    fun provideInvoiceItemDao(database: InvoicyDatabase): InvoiceItemDao {
        return database.invoiceItemDao()
    }
    
    @Provides
    fun provideQuoteDao(database: InvoicyDatabase): QuoteDao {
        return database.quoteDao()
    }
    
    @Provides
    fun provideQuoteItemDao(database: InvoicyDatabase): QuoteItemDao {
        return database.quoteItemDao()
    }
}
