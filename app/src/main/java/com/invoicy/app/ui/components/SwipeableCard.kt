@file:OptIn(ExperimentalMaterial3Api::class)

package com.invoicy.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Carte avec menu d'actions
 */
@Composable
fun ActionableCard(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: (() -> Unit)? = null,
    onView: (() -> Unit)? = null,
    onMarkPaid: (() -> Unit)? = null,
    onSend: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                content()
            }
            
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                }
                
                CardActionsMenu(
                    expanded = showMenu,
                    onDismiss = { showMenu = false },
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onDuplicate = onDuplicate,
                    onView = onView,
                    onMarkPaid = onMarkPaid,
                    onSend = onSend
                )
            }
        }
    }
}

/**
 * Menu d'actions pour les cartes
 */
@Composable
fun CardActionsMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: (() -> Unit)? = null,
    onView: (() -> Unit)? = null,
    onMarkPaid: (() -> Unit)? = null,
    onSend: (() -> Unit)? = null
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss
    ) {
        onView?.let {
            DropdownMenuItem(
                text = { Text("Voir") },
                onClick = {
                    it()
                    onDismiss()
                },
                leadingIcon = {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                }
            )
        }
        
        DropdownMenuItem(
            text = { Text("Modifier") },
            onClick = {
                onEdit()
                onDismiss()
            },
            leadingIcon = {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
        )
        
        onDuplicate?.let {
            DropdownMenuItem(
                text = { Text("Dupliquer") },
                onClick = {
                    it()
                    onDismiss()
                },
                leadingIcon = {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                }
            )
        }
        
        onMarkPaid?.let {
            DropdownMenuItem(
                text = { Text("Marquer payée") },
                onClick = {
                    it()
                    onDismiss()
                },
                leadingIcon = {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                }
            )
        }
        
        onSend?.let {
            DropdownMenuItem(
                text = { Text("Envoyer") },
                onClick = {
                    it()
                    onDismiss()
                },
                leadingIcon = {
                    Icon(Icons.Default.Send, contentDescription = null)
                }
            )
        }
        
        Divider()
        
        DropdownMenuItem(
            text = { Text("Supprimer") },
            onClick = {
                onDelete()
                onDismiss()
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            colors = MenuDefaults.itemColors(
                textColor = MaterialTheme.colorScheme.error
            )
        )
    }
}
