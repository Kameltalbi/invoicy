package com.invoicy.app.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.invoicy.app.data.entity.InvoiceStatus
import com.invoicy.app.data.entity.QuoteStatus

/**
 * Barre de résumé compacte pour documents (Factures/Devis)
 */
@Composable
fun DocumentSummaryBar(
    modifier: Modifier = Modifier,
    stats: List<SummaryStatItem>
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            MiniSummaryCard(
                title = stat.title,
                value = stat.value,
                icon = stat.icon,
                color = stat.color
            )
        }
    }
}

data class SummaryStatItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
private fun MiniSummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.08f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
        }
    }
}

/**
 * Filtres rapides en chips horizontaux
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusFilterChips(
    modifier: Modifier = Modifier,
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            FilterChip(
                selected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) },
                leadingIcon = if (filter == selectedFilter) {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null
            )
        }
    }
}

/**
 * Badge de statut coloré
 */
@Composable
fun StatusBadge(
    status: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Obtenir la couleur selon le statut de facture
 */
fun getInvoiceStatusColor(status: InvoiceStatus): Color {
    return when (status) {
        InvoiceStatus.PAID -> Color(0xFF16A34A) // Vert
        InvoiceStatus.SENT -> Color(0xFF2D6CDF) // Bleu
        InvoiceStatus.OVERDUE -> Color(0xFFDC2626) // Rouge
        InvoiceStatus.DRAFT -> Color(0xFF6B7280) // Gris
    }
}

/**
 * Obtenir le texte du statut de facture
 */
fun getInvoiceStatusText(status: InvoiceStatus): String {
    return when (status) {
        InvoiceStatus.PAID -> "Payée"
        InvoiceStatus.SENT -> "Envoyée"
        InvoiceStatus.OVERDUE -> "En retard"
        InvoiceStatus.DRAFT -> "Brouillon"
    }
}

/**
 * Obtenir la couleur selon le statut de devis
 */
fun getQuoteStatusColor(status: QuoteStatus): Color {
    return when (status) {
        QuoteStatus.ACCEPTED -> Color(0xFF16A34A) // Vert
        QuoteStatus.SENT -> Color(0xFF2D6CDF) // Bleu
        QuoteStatus.REJECTED -> Color(0xFFDC2626) // Rouge
        QuoteStatus.DRAFT -> Color(0xFF6B7280) // Gris
    }
}

/**
 * Obtenir le texte du statut de devis
 */
fun getQuoteStatusText(status: QuoteStatus): String {
    return when (status) {
        QuoteStatus.ACCEPTED -> "Accepté"
        QuoteStatus.SENT -> "Envoyé"
        QuoteStatus.REJECTED -> "Refusé"
        QuoteStatus.DRAFT -> "Brouillon"
    }
}

/**
 * Header de page document avec recherche et filtre
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentHeader(
    title: String,
    buttonText: String = "Nouveau",
    onAddClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "Rechercher")
            }
            IconButton(onClick = onFilterClick) {
                Icon(Icons.Default.FilterList, contentDescription = "Filtrer")
            }
            FilledTonalButton(
                onClick = onAddClick,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(buttonText)
            }
        }
    }
}
