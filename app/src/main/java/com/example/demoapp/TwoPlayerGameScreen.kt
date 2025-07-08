package com.example.demoapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TwoPlayerGameScreen(navController: NavController) {
    var board by remember { mutableStateOf(Array(3) { Array(3) { "" } }) }
    var currentPlayer by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (winner != null) "Player $winner wins!"
            else "Player $currentPlayer's turn",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (winner != null) Color(0xFF4CAF50) else Color(0xFF1E88E5),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Game board
        Column(
            modifier = Modifier
                .border(2.dp, Color(0xFF1E88E5))
                .background(Color.White)
        ) {
            for (i in 0..2) {
                Row {
                    for (j in 0..2) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .border(1.dp, Color.LightGray)
                                .clickable {
                                    if (board[i][j].isEmpty() && winner == null) {
                                        board = board.copyOf().also { it[i][j] = currentPlayer }
                                        checkWinner(board).let {
                                            if (it != null) {
                                                winner = it
                                            } else if (board.all { row -> row.all { it.isNotEmpty() } }) {
                                                winner = "Draw"
                                            } else {
                                                currentPlayer = if (currentPlayer == "X") "O" else "X"
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = board[i][j],
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (board[i][j] == "X") Color(0xFF1E88E5) else Color(0xFFE53935)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                board = Array(3) { Array(3) { "" } }
                currentPlayer = "X"
                winner = null
            },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF1E88E5)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Restart Game", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF757575)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Back to Menu", color = Color.White)
        }
    }
}

private fun checkWinner(board: Array<Array<String>>): String? {
    // Check rows
    for (i in 0..2) {
        if (board[i][0].isNotEmpty() && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
            return board[i][0]
        }
    }

    // Check columns
    for (j in 0..2) {
        if (board[0][j].isNotEmpty() && board[0][j] == board[1][j] && board[0][j] == board[2][j]) {
            return board[0][j]
        }
    }

    // Check diagonals
    if (board[0][0].isNotEmpty() && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
        return board[0][0]
    }
    if (board[0][2].isNotEmpty() && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
        return board[0][2]
    }

    return null
}