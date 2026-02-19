package com.invoicy.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.invoicy.app.R
import com.invoicy.app.data.entity.InvoiceStatus
import com.invoicy.app.data.entity.InvoiceWithDetails
import com.invoicy.app.ui.viewmodel.InvoiceViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Écran de la liste des factures
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToInvoice: (Long) -> Unit,
    onNavigateToNewInvoice: () -> Unit,
    viewModel: InvoiceViewModel = hiltViewModel()
) {
    val invoices by viewModel.invoices.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.invoices_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.createNewInvoice()
                    onNavigateToNewInvoice()
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Invoice")
            }
        }
    ) { paddingValues ->
        if (invoices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Text(
                        text = stringResource(R.string.msg_no_data),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(invoices, key = { it.invoice.id }) { invoice ->
                    InvoiceCard(
                        invoice = invoice,
                        onClick = { onNavigateToInvoice(invoice.invoice.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun InvoiceCard(
    invoice: InvoiceWithDetails,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = invoice.invoice.number,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                InvoiceStatusChip(status = invoice.invoice.status)
            }
            
            Text(
                text = invoice.client.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dateFormat.format(Date(invoice.invoice.issueDate)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = String.format("%.2f €", invoice.getTotal()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun InvoiceStatusChip(status: InvoiceStatus) {
    val (text, color) = when (status) {
        InvoiceStatus.DRAFT -> stringResource(R.string.status_draft) to MaterialTheme.colorScheme.outline
        InvoiceStatus.SENT -> stringResource(R.string.status_sent) to MaterialTheme.colorScheme.primary
        InvoiceStatus.PAID -> stringResource(R.string.status_paid) to MaterialTheme.colorScheme.tertiary
        InvoiceStatus.OVERDUE -> stringResource(R.string.status_overdue) to MaterialTheme.colorScheme.error
    }
    
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
