package com.invoicy.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper pour partager les PDF via différents canaux
 */
@Singleton
class ShareHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    /**
     * Partage un PDF via le share sheet Android
     */
    fun sharePdf(file: File, title: String = "Partager le PDF") {
        val uri = getFileUri(file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, title).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
    
    /**
     * Envoie un PDF par email
     */
    fun sendPdfByEmail(file: File, subject: String, body: String = "") {
        val uri = getFileUri(file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        try {
            context.startActivity(Intent.createChooser(intent, "Envoyer par email").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Envoie un PDF via WhatsApp
     */
    fun sendPdfViaWhatsApp(file: File) {
        val uri = getFileUri(file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            `package` = "com.whatsapp"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // WhatsApp non installé, utiliser le share sheet
            sharePdf(file, "Partager via WhatsApp")
        }
    }
    
    /**
     * Copie le PDF dans le dossier Downloads
     */
    fun savePdfToDownloads(file: File): Boolean {
        return try {
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(
                android.os.Environment.DIRECTORY_DOWNLOADS
            )
            val destFile = File(downloadsDir, file.name)
            file.copyTo(destFile, overwrite = true)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    private fun getFileUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
