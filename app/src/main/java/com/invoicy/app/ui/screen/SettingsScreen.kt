package com.invoicy.app.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.invoicy.app.ui.viewmodel.SettingsViewModel

/**
 * Écran des paramètres
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val companyName by viewModel.companyName.collectAsState()
    val email by viewModel.email.collectAsState()
    val phone by viewModel.phone.collectAsState()
    val address by viewModel.address.collectAsState()
    val taxNumber by viewModel.taxNumber.collectAsState()
    val currency by viewModel.currency.collectAsState()
    val defaultFooter by viewModel.defaultFooter.collectAsState()
    val language by viewModel.language.collectAsState()
    val theme by viewModel.theme.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateLogoUri(it) }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.settings_profile),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { viewModel.updateCompanyName(it) },
                    label = { Text(stringResource(R.string.settings_company_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) }
                )
            }
            
            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text(stringResource(R.string.settings_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                )
            }
            
            item {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { viewModel.updatePhone(it) },
                    label = { Text(stringResource(R.string.settings_phone)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                )
            }
            
            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { viewModel.updateAddress(it) },
                    label = { Text(stringResource(R.string.settings_address)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    minLines = 2
                )
            }
            
            item {
                OutlinedTextField(
                    value = taxNumber,
                    onValueChange = { viewModel.updateTaxNumber(it) },
                    label = { Text(stringResource(R.string.settings_tax_number)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null) }
                )
            }
            
            item {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.settings_upload_logo))
                }
            }
            
            item {
                OutlinedTextField(
                    value = defaultFooter,
                    onValueChange = { viewModel.updateDefaultFooter(it) },
                    label = { Text(stringResource(R.string.settings_footer)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
            
            item {
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Préférences",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            
            item {
                SettingItem(
                    title = stringResource(R.string.settings_language),
                    value = when (language) {
                        "fr" -> stringResource(R.string.lang_french)
                        "ar" -> stringResource(R.string.lang_arabic)
                        else -> stringResource(R.string.lang_english)
                    },
                    icon = Icons.Default.Language,
                    onClick = { showLanguageDialog = true }
                )
            }
            
            item {
                SettingItem(
                    title = stringResource(R.string.settings_theme),
                    value = when (theme) {
                        "light" -> stringResource(R.string.theme_light)
                        "dark" -> stringResource(R.string.theme_dark)
                        else -> stringResource(R.string.theme_system)
                    },
                    icon = Icons.Default.Palette,
                    onClick = { showThemeDialog = true }
                )
            }
            
            item {
                SettingItem(
                    title = stringResource(R.string.settings_currency),
                    value = currency,
                    icon = Icons.Default.AttachMoney,
                    onClick = { showCurrencyDialog = true }
                )
            }
            
            if (!isPremium) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.premium_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.premium_desc),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Button(
                                onClick = { viewModel.upgradeToPremium() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(stringResource(R.string.premium_upgrade))
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showLanguageDialog) {
        LanguageDialog(
            currentLanguage = language,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { 
                viewModel.updateLanguage(it)
                showLanguageDialog = false
            }
        )
    }
    
    if (showThemeDialog) {
        ThemeDialog(
            currentTheme = theme,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { 
                viewModel.updateTheme(it)
                showThemeDialog = false
            }
        )
    }
    
    if (showCurrencyDialog) {
        CurrencyDialog(
            currentCurrency = currency,
            onDismiss = { showCurrencyDialog = false },
            onCurrencySelected = { 
                viewModel.updateCurrency(it)
                showCurrencyDialog = false
            }
        )
    }
}

@Composable
fun SettingItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
fun LanguageDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_language)) },
        text = {
            Column {
                RadioOption("en", stringResource(R.string.lang_english), currentLanguage, onLanguageSelected)
                RadioOption("fr", stringResource(R.string.lang_french), currentLanguage, onLanguageSelected)
                RadioOption("ar", stringResource(R.string.lang_arabic), currentLanguage, onLanguageSelected)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun ThemeDialog(
    currentTheme: String,
    onDismiss: () -> Unit,
    onThemeSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_theme)) },
        text = {
            Column {
                RadioOption("system", stringResource(R.string.theme_system), currentTheme, onThemeSelected)
                RadioOption("light", stringResource(R.string.theme_light), currentTheme, onThemeSelected)
                RadioOption("dark", stringResource(R.string.theme_dark), currentTheme, onThemeSelected)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun CurrencyDialog(
    currentCurrency: String,
    onDismiss: () -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    val currencies = listOf("EUR", "USD", "GBP", "DZD", "MAD", "TND")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.settings_currency)) },
        text = {
            Column {
                currencies.forEach { currency ->
                    RadioOption(currency, currency, currentCurrency, onCurrencySelected)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
fun RadioOption(
    value: String,
    label: String,
    selectedValue: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = value == selectedValue,
            onClick = { onSelected(value) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label)
    }
}
