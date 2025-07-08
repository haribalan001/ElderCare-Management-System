package com.example.demoapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.demoapp.DatabaseHelper

// App-wide settings state
class AppSettings {
    var darkModeEnabled by mutableStateOf(false)
    var fontSizeMultiplier by mutableStateOf(1.0f)
    var notificationEnabled by mutableStateOf(true)
    var vibrationEnabled by mutableStateOf(true)
    var language by mutableStateOf("English")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    dbHelper: DatabaseHelper,
    email: String,
    appSettings: AppSettings = remember { AppSettings() }
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val languages = listOf("English", "Spanish", "French", "German")

    // Apply the current theme based on settings
    val colorScheme = if (appSettings.darkModeEnabled) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography.copy(
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp * appSettings.fontSizeMultiplier
            )
        )
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Account Section
                SettingsSection(title = "Account") {
                    SettingItem(
                        name = "Email",
                        value = email,
                        showDivider = false
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showLogoutDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Logout")
                    }
                }

                // Display Section
                SettingsSection(title = "Display") {
                    SettingItem(
                        name = "Dark Mode",
                        action = {
                            Switch(
                                checked = appSettings.darkModeEnabled,
                                onCheckedChange = { appSettings.darkModeEnabled = it }
                            )
                        }
                    )
                    SettingItem(
                        name = "Font Size",
                        action = {
                            Slider(
                                value = appSettings.fontSizeMultiplier,
                                onValueChange = { appSettings.fontSizeMultiplier = it },
                                valueRange = 0.8f..1.5f,
                                steps = 7,
                                modifier = Modifier.width(150.dp)
                            )
                            Text(
                                text = "%.1f".format(appSettings.fontSizeMultiplier),
                                modifier = Modifier.width(40.dp)
                            )
                        }
                    )
                }

                // Notifications Section
                SettingsSection(title = "Notifications") {
                    SettingItem(
                        name = "Enable Notifications",
                        action = {
                            Switch(
                                checked = appSettings.notificationEnabled,
                                onCheckedChange = { appSettings.notificationEnabled = it }
                            )
                        }
                    )
                    SettingItem(
                        name = "Vibration",
                        action = {
                            Switch(
                                checked = appSettings.vibrationEnabled,
                                onCheckedChange = { appSettings.vibrationEnabled = it }
                            )
                        }
                    )
                }

                // Language Section
                SettingsSection(title = "Language") {
                    var expanded by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        SettingItem(
                            name = "App Language",
                            value = appSettings.language,
                            onClick = { expanded = true }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            languages.forEach { language ->
                                DropdownMenuItem(
                                    text = { Text(language) },
                                    onClick = {
                                        appSettings.language = language
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // About Section
                SettingsSection(title = "About") {
                    SettingItem(
                        name = "Version",
                        value = "1.0.0",
                        showDivider = true
                    )
                    SettingItem(
                        name = "Terms of Service",
                        onClick = { /* Navigate to terms screen */ },
                        showDivider = true
                    )
                    SettingItem(
                        name = "Privacy Policy",
                        onClick = { /* Navigate to privacy screen */ },
                        showDivider = false
                    )
                }
            }
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Confirm Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            dbHelper.logoutUser(email)
                            navController.navigate("loginScreen") { popUpTo(0) }
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                content = content
            )
        }
    }
}

@Composable
fun SettingItem(
    name: String,
    value: String? = null,
    onClick: (() -> Unit)? = null,
    action: @Composable (() -> Unit)? = null,
    showDivider: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium
                )
                value?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            action?.invoke()
        }
        if (showDivider) {
            Divider(
                modifier = Modifier.padding(start = if (value != null) 0.dp else 16.dp),
                thickness = 0.5.dp
            )
        }
    }
}