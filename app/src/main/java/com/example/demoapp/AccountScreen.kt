package com.example.demoapp.screens

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    navController: NavController,
    email: String,
    dbHelper: DatabaseHelper
) {
    var user by remember { mutableStateOf(dbHelper.getUserProfile(email)) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Account") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Profile Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    ProfileInfoItem("Name", user?.name ?: "Not available")
                    ProfileInfoItem("Email", user?.email ?: "Not available")
                    ProfileInfoItem("Mobile", user?.mobile ?: "Not available")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Actions Section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    SettingsButton(
                        text = "Edit Profile",
                        onClick = { navController.navigate("editProfile/$email") }
                    )
                    Divider()
                    SettingsButton(
                        text = "Change Password",
                        onClick = { navController.navigate("changePassword/$email") }
                    )
                    Divider()
                    SettingsButton(
                        text = "Logout",
                        onClick = { showLogoutDialog = true },
                        textColor = Color.Red
                    )
                }
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
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                ) {
                    Text("Logout", color = Color.Red)
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

@Composable
fun ProfileInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun SettingsButton(
    text: String,
    onClick: () -> Unit,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            modifier = Modifier.fillMaxWidth()
        )
    }
}