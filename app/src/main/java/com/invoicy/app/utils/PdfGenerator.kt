package com.invoicy.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.invoicy.app.data.entity.InvoiceWithDetails
import com.invoicy.app.data.entity.QuoteWithDetails
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service de génération de PDF pour les factures et devis
 */
@Singleton
class PdfGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    /**
     * Génère un PDF pour une facture
     */
    fun generateInvoicePdf(
        invoice: InvoiceWithDetails,
        companyName: String,
        email: String,
        phone: String,
        address: String,
        taxNumber: String,
        logoUri: String?,
        currency: String,
        footer: String
    ): File {
        val fileName = "invoice_${invoice.invoice.number}_${System.currentTimeMillis()}.pdf"
        val file = File(context.filesDir, "invoices/$fileName")
        file.parentFile?.mkdirs()
        
        val writer = PdfWriter(file)
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc, PageSize.A4)
        document.setMargins(40f, 40f, 40f, 40f)
        
        // Couleur principale
        val primaryColor = DeviceRgb(98, 0, 238)
        
        // En-tête avec logo et infos émetteur
        addHeader(document, companyName, email, phone, address, taxNumber, logoUri, primaryColor)
        
        // Titre
        val title = Paragraph("FACTURE")
            .setFontSize(24f)
            .setBold()
            .setFontColor(primaryColor)
            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginTop(20f)
        document.add(title)
        
        // Numéro et dates
        val infoTable = Table(2).useAllAvailableWidth()
        infoTable.addCell(createCell("N° Facture:", true))
        infoTable.addCell(createCell(invoice.invoice.number, false))
        infoTable.addCell(createCell("Date d'émission:", true))
        infoTable.addCell(createCell(dateFormat.format(Date(invoice.invoice.issueDate)), false))
        infoTable.addCell(createCell("Date d'échéance:", true))
        infoTable.addCell(createCell(dateFormat.format(Date(invoice.invoice.dueDate)), false))
        document.add(infoTable.setMarginTop(10f))
        
        // Informations client
        document.add(Paragraph("Facturé à:").setBold().setMarginTop(20f))
        val clientInfo = Paragraph()
            .add(invoice.client.name + "\n")
            .add(invoice.client.email + "\n")
            .add(invoice.client.phone + "\n")
            .add(invoice.client.address + "\n")
            .add(invoice.client.country)
            .setFontSize(10f)
        document.add(clientInfo)
        
        // Tableau des prestations
        val itemsTable = Table(floatArrayOf(3f, 1f, 1.5f, 1f, 1.5f))
            .useAllAvailableWidth()
            .setMarginTop(20f)
        
        // En-têtes du tableau
        itemsTable.addHeaderCell(createHeaderCell("Description"))
        itemsTable.addHeaderCell(createHeaderCell("Qté"))
        itemsTable.addHeaderCell(createHeaderCell("P.U. HT"))
        itemsTable.addHeaderCell(createHeaderCell("TVA"))
        itemsTable.addHeaderCell(createHeaderCell("Total TTC"))
        
        // Lignes de prestation
        invoice.items.forEach { item ->
            itemsTable.addCell(createCell(item.description, false))
            itemsTable.addCell(createCell(formatNumber(item.quantity), false))
            itemsTable.addCell(createCell(formatCurrency(item.unitPrice, currency), false))
            itemsTable.addCell(createCell("${formatNumber(item.vatRate)}%", false))
            itemsTable.addCell(createCell(formatCurrency(item.getTotal(), currency), false))
        }
        
        document.add(itemsTable)
        
        // Totaux
        val totalsTable = Table(2).useAllAvailableWidth()
            .setMarginTop(20f)
            .setWidth(UnitValue.createPercentValue(40f))
            .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT)
        
        totalsTable.addCell(createCell("Sous-total HT:", true))
        totalsTable.addCell(createCell(formatCurrency(invoice.getSubtotal(), currency), false))
        
        if (invoice.invoice.discount > 0) {
            totalsTable.addCell(createCell("Remise:", true))
            totalsTable.addCell(createCell("-${formatCurrency(invoice.getDiscountAmount(), currency)}", false))
        }
        
        totalsTable.addCell(createCell("TVA:", true))
        totalsTable.addCell(createCell(formatCurrency(invoice.getVatTotal(), currency), false))
        
        totalsTable.addCell(createCell("Total TTC:", true).setBold().setFontSize(12f))
        totalsTable.addCell(createCell(formatCurrency(invoice.getTotal(), currency), false).setBold().setFontSize(12f))
        
        document.add(totalsTable)
        
        // Notes
        if (invoice.invoice.notes.isNotBlank()) {
            document.add(Paragraph("Notes:").setBold().setMarginTop(20f))
            document.add(Paragraph(invoice.invoice.notes).setFontSize(9f))
        }
        
        // Pied de page
        if (footer.isNotBlank()) {
            val footerParagraph = Paragraph(footer)
                .setFontSize(8f)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(40f, 20f, PageSize.A4.width - 80f)
            document.add(footerParagraph)
        }
        
        document.close()
        return file
    }
    
    /**
     * Génère un PDF pour un devis
     */
    fun generateQuotePdf(
        quote: QuoteWithDetails,
        companyName: String,
        email: String,
        phone: String,
        address: String,
        taxNumber: String,
        logoUri: String?,
        currency: String,
        footer: String
    ): File {
        val fileName = "quote_${quote.quote.number}_${System.currentTimeMillis()}.pdf"
        val file = File(context.filesDir, "quotes/$fileName")
        file.parentFile?.mkdirs()
        
        val writer = PdfWriter(file)
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc, PageSize.A4)
        document.setMargins(40f, 40f, 40f, 40f)
        
        val primaryColor = DeviceRgb(98, 0, 238)
        
        addHeader(document, companyName, email, phone, address, taxNumber, logoUri, primaryColor)
        
        val title = Paragraph("DEVIS")
            .setFontSize(24f)
            .setBold()
            .setFontColor(primaryColor)
            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginTop(20f)
        document.add(title)
        
        val infoTable = Table(2).useAllAvailableWidth()
        infoTable.addCell(createCell("N° Devis:", true))
        infoTable.addCell(createCell(quote.quote.number, false))
        infoTable.addCell(createCell("Date d'émission:", true))
        infoTable.addCell(createCell(dateFormat.format(Date(quote.quote.issueDate)), false))
        infoTable.addCell(createCell("Valable jusqu'au:", true))
        infoTable.addCell(createCell(dateFormat.format(Date(quote.quote.validUntil)), false))
        document.add(infoTable.setMarginTop(10f))
        
        document.add(Paragraph("Client:").setBold().setMarginTop(20f))
        val clientInfo = Paragraph()
            .add(quote.client.name + "\n")
            .add(quote.client.email + "\n")
            .add(quote.client.phone + "\n")
            .add(quote.client.address + "\n")
            .add(quote.client.country)
            .setFontSize(10f)
        document.add(clientInfo)
        
        val itemsTable = Table(floatArrayOf(3f, 1f, 1.5f, 1f, 1.5f))
            .useAllAvailableWidth()
            .setMarginTop(20f)
        
        itemsTable.addHeaderCell(createHeaderCell("Description"))
        itemsTable.addHeaderCell(createHeaderCell("Qté"))
        itemsTable.addHeaderCell(createHeaderCell("P.U. HT"))
        itemsTable.addHeaderCell(createHeaderCell("TVA"))
        itemsTable.addHeaderCell(createHeaderCell("Total TTC"))
        
        quote.items.forEach { item ->
            itemsTable.addCell(createCell(item.description, false))
            itemsTable.addCell(createCell(formatNumber(item.quantity), false))
            itemsTable.addCell(createCell(formatCurrency(item.unitPrice, currency), false))
            itemsTable.addCell(createCell("${formatNumber(item.vatRate)}%", false))
            itemsTable.addCell(createCell(formatCurrency(item.getTotal(), currency), false))
        }
        
        document.add(itemsTable)
        
        val totalsTable = Table(2).useAllAvailableWidth()
            .setMarginTop(20f)
            .setWidth(UnitValue.createPercentValue(40f))
            .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT)
        
        totalsTable.addCell(createCell("Sous-total HT:", true))
        totalsTable.addCell(createCell(formatCurrency(quote.getSubtotal(), currency), false))
        
        if (quote.quote.discount > 0) {
            totalsTable.addCell(createCell("Remise:", true))
            totalsTable.addCell(createCell("-${formatCurrency(quote.getDiscountAmount(), currency)}", false))
        }
        
        totalsTable.addCell(createCell("TVA:", true))
        totalsTable.addCell(createCell(formatCurrency(quote.getVatTotal(), currency), false))
        
        totalsTable.addCell(createCell("Total TTC:", true).setBold().setFontSize(12f))
        totalsTable.addCell(createCell(formatCurrency(quote.getTotal(), currency), false).setBold().setFontSize(12f))
        
        document.add(totalsTable)
        
        if (quote.quote.notes.isNotBlank()) {
            document.add(Paragraph("Notes:").setBold().setMarginTop(20f))
            document.add(Paragraph(quote.quote.notes).setFontSize(9f))
        }
        
        if (footer.isNotBlank()) {
            val footerParagraph = Paragraph(footer)
                .setFontSize(8f)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedPosition(40f, 20f, PageSize.A4.width - 80f)
            document.add(footerParagraph)
        }
        
        document.close()
        return file
    }
    
    private fun addHeader(
        document: Document,
        companyName: String,
        email: String,
        phone: String,
        address: String,
        taxNumber: String,
        logoUri: String?,
        primaryColor: DeviceRgb
    ) {
        val headerTable = Table(2).useAllAvailableWidth()
        
        // Logo (si disponible)
        if (logoUri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(Uri.parse(logoUri))
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imageData = ImageDataFactory.create(stream.toByteArray())
                val logo = Image(imageData).setWidth(80f)
                headerTable.addCell(Cell().add(logo).setBorder(Border.NO_BORDER))
            } catch (e: Exception) {
                headerTable.addCell(Cell().setBorder(Border.NO_BORDER))
            }
        } else {
            headerTable.addCell(Cell().setBorder(Border.NO_BORDER))
        }
        
        // Infos émetteur
        val companyInfo = Paragraph()
            .add(Paragraph(companyName).setBold().setFontSize(14f).setFontColor(primaryColor))
            .add("\n")
            .add(email + "\n")
            .add(phone + "\n")
            .add(address + "\n")
            .add("N° fiscal: $taxNumber")
            .setFontSize(9f)
            .setTextAlignment(TextAlignment.RIGHT)
        
        headerTable.addCell(Cell().add(companyInfo).setBorder(Border.NO_BORDER))
        document.add(headerTable)
    }
    
    private fun createHeaderCell(text: String): Cell {
        return Cell()
            .add(Paragraph(text).setBold())
            .setBackgroundColor(DeviceRgb(240, 240, 240))
            .setFontSize(10f)
            .setPadding(5f)
    }
    
    private fun createCell(text: String, bold: Boolean): Cell {
        val paragraph = Paragraph(text).setFontSize(9f)
        if (bold) paragraph.setBold()
        return Cell().add(paragraph).setPadding(5f)
    }
    
    private fun formatCurrency(amount: Double, currency: String): String {
        return String.format("%.2f %s", amount, currency)
    }
    
    private fun formatNumber(number: Double): String {
        return if (number % 1.0 == 0.0) {
            number.toInt().toString()
        } else {
            String.format("%.2f", number)
        }
    }
}
