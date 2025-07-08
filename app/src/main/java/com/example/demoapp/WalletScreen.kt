package com.example.demoapp.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.demoapp.DatabaseHelper
import com.example.demoapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun WalletScreen(navController: NavController, dbHelper: DatabaseHelper, userId: Int) {
    val context = LocalContext.current
    var balance by remember { mutableStateOf(0) }
    LaunchedEffect(userId) {
        withContext(Dispatchers.IO) {
            balance = dbHelper.getWalletBalance(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("My Wallet", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Spacer(modifier = Modifier.height(20.dp))

        // Wallet Balance Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Wallet Balance", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text("₹ $balance", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Add Money Button
        Button(
            onClick = { navController.navigate("paymentScreen/$userId") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF1E88E5))
        ) {
            Text("Add Money", fontSize = 18.sp, color = Color.White)
        }
    }
}
