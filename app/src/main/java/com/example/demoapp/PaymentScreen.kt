package com.example.demoapp

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PaymentScreen(navController: NavController, amount: Int) {
    val activity = navController.context as Activity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment", color = Color.White) },
                backgroundColor = Color(0xFF1976D2)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Total Amount: ₹$amount", style = MaterialTheme.typography.h5)

            Spacer(modifier = Modifier.height(20.dp))

            // Display QR Code Image (Replace 'your_qr_image' with your actual QR image resource)
            Image(
                painter = painterResource(id = R.drawable.your_qr_image), // Add your QR code image in res/drawable
                contentDescription = "Payment QR Code",
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Scan the QR code above to complete your payment.",
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Button to Confirm Payment (Once scanned)
            Button(
                onClick = { showPaymentStatus(activity) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Green)
            ) {
                Text("Payment Done", color = Color.White)
            }
        }
    }
}

// Function to simulate showing payment success
fun showPaymentStatus(activity: Activity) {
    Toast.makeText(activity, "Payment Successful! Amount added to wallet.", Toast.LENGTH_SHORT).show()
}
