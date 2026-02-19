package com.invoicy.app.data.database

import androidx.room.TypeConverter
import com.invoicy.app.data.entity.*

/**
 * Convertisseurs de types pour Room
 */
class Converters {
    
    @TypeConverter
    fun fromInvoiceStatus(value: InvoiceStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toInvoiceStatus(value: String): InvoiceStatus {
        return InvoiceStatus.valueOf(value)
    }
    
    @TypeConverter
    fun fromQuoteStatus(value: QuoteStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toQuoteStatus(value: String): QuoteStatus {
        return QuoteStatus.valueOf(value)
    }
    
    @TypeConverter
    fun fromDiscountType(value: DiscountType): String {
        return value.name
    }
    
    @TypeConverter
    fun toDiscountType(value: String): DiscountType {
        return DiscountType.valueOf(value)
    }
}
