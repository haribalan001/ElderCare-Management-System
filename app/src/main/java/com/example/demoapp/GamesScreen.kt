package com.example.demoapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.demoapp.DatabaseHelper

// Replace with your preferred color code
 @Composable
 fun GamesScreen(navController: NavController, dbHelper: DatabaseHelper, email: String, context: Context) {
     Box(
         modifier = Modifier
             .fillMaxSize()
             .padding(horizontal = 16.dp) // Horizontal padding
     ) {
         // Main content in the center
         Column(
             modifier = Modifier
                 .fillMaxSize()
                 .padding(bottom = 72.dp), // Reserve space for the footer
             verticalArrangement = Arrangement.Center,
             horizontalAlignment = Alignment.CenterHorizontally
         ) {
             Text(
                 text = "Choose Your Game Mode",
                 fontSize = 24.sp,
                 fontWeight = FontWeight.Bold,
                 color = primaryColor,
                 modifier = Modifier.padding(bottom = 24.dp)
             )

             Row(
                 modifier = Modifier.fillMaxWidth(),
                 horizontalArrangement = Arrangement.SpaceEvenly
             ) {
                 HoverableButton(
                     text = "1P Game",
                     backgroundColor = Color(0xFF6200EA), // Purple color for 1P Games
                     onClick = { navController.navigate("1pGamesScreen") }
                 )
                 HoverableButton(
                     text = "2P Game",
                     backgroundColor = Color(0xFF018786), // Teal color for 2P Games
                     onClick = { navController.navigate("2pGamesScreen") }
                 )
             }
         }

         // Footer Section
         Box(
             modifier = Modifier
                 .fillMaxWidth()
                 .align(Alignment.BottomCenter)
         ) {
             Footer(navController, email, dbHelper, context)
         }
     }
 }

@Composable
fun HoverableButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (text == "1P Game") Icons.Default.Person else Icons.Default.People,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

