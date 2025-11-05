package com.app.shamba_bora.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Account Section
            item {
                SettingsSectionHeader("Account")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Profile",
                    subtitle = "Manage your profile information",
                    onClick = onNavigateToProfile
                )
            }
            
            item {
                Divider()
            }
            
            // Preferences Section
            item {
                SettingsSectionHeader("Preferences")
            }
            
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Enable push notifications",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }
            
            item {
                Divider()
            }
            
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Use dark theme",
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )
            }
            
            item {
                Divider()
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "Language",
                    subtitle = "English",
                    onClick = { /* Navigate to language selection */ }
                )
            }
            
            item {
                Divider()
            }
            
            // App Info Section
            item {
                SettingsSectionHeader("App Info")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "About",
                    subtitle = "Version 1.0.0",
                    onClick = { /* Show about dialog */ }
                )
            }
            
            item {
                Divider()
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "View our privacy policy",
                    onClick = { /* Open privacy policy */ }
                )
            }
            
            item {
                Divider()
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Terms of Service",
                    subtitle = "View terms and conditions",
                    onClick = { /* Open terms */ }
                )
            }
            
            item {
                Divider()
            }
            
            // Danger Zone
            item {
                SettingsSectionHeader("Danger Zone")
            }
            
            item {
                SettingsItem(
                    icon = Icons.Default.Logout,
                    title = "Logout",
                    subtitle = "Sign out of your account",
                    onClick = { showLogoutDialog = true },
                    textColor = MaterialTheme.colorScheme.error
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text("Logout")
            },
            text = {
                Text("Are you sure you want to logout?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = textColor
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f)
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
