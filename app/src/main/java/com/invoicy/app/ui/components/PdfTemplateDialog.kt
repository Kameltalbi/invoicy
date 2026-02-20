package com.invoicy.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Dialog pour sélectionner le template PDF
 */
@Composable
fun PdfTemplateDialog(
    currentTemplate: String,
    onDismiss: () -> Unit,
    onTemplateSelected: (String) -> Unit
) {
    var selectedTemplate by remember { mutableStateOf(currentTemplate) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Description, contentDescription = null) },
        title = { Text("Template PDF") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Choisissez le style de vos factures et devis (Format A4)",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                // Template Classique
                TemplateOption(
                    title = "Classique",
                    description = "Sobre et professionnel - Idéal pour un usage standard",
                    icon = Icons.Default.Description,
                    isSelected = selectedTemplate == "classic",
                    onClick = { selectedTemplate = "classic" }
                )
                
                // Template Moderne
                TemplateOption(
                    title = "Moderne",
                    description = "Design SaaS coloré - Pour une image moderne",
                    icon = Icons.Default.AutoAwesome,
                    isSelected = selectedTemplate == "modern",
                    onClick = { selectedTemplate = "modern" }
                )
                
                // Template Minimal
                TemplateOption(
                    title = "Minimal",
                    description = "Épuré et simple - Noir et blanc élégant",
                    icon = Icons.Default.Circle,
                    isSelected = selectedTemplate == "minimal",
                    onClick = { selectedTemplate = "minimal" }
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Text(
                    text = "ℹ️ Toutes les informations légales obligatoires sont incluses dans tous les templates",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onTemplateSelected(selectedTemplate) }) {
                Text("Appliquer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
private fun TemplateOption(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Sélectionné",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
