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
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun OnePlayerGameScreen(navController: NavController) {
    var board by remember { mutableStateOf(Array(3) { Array(3) { "" } }) }
    var winner by remember { mutableStateOf<String?>(null) }
    var isPlayerTurn by remember { mutableStateOf(true) }

    fun makeComputerMove() {
        val emptyCells = mutableListOf<Pair<Int, Int>>()
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].isEmpty()) {
                    emptyCells.add(Pair(i, j))
                }
            }
        }

        if (emptyCells.isNotEmpty() && winner == null) {
            val (i, j) = emptyCells[Random.nextInt(emptyCells.size)]
            board = board.copyOf().also { it[i][j] = "O" }
            checkWinner(board).let {
                if (it != null) {
                    winner = it
                } else if (board.all { row -> row.all { it.isNotEmpty() } }) {
                    winner = "Draw"
                } else {
                    isPlayerTurn = true
                }
            }
        }
    }

    LaunchedEffect(isPlayerTurn) {
        if (!isPlayerTurn && winner == null) {
            delay(500) // Small delay for better UX
            makeComputerMove()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                winner == "X" -> "You win!"
                winner == "O" -> "Computer wins!"
                winner == "Draw" -> "It's a draw!"
                isPlayerTurn -> "Your turn (X)"
                else -> "Computer thinking..."
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = when {
                winner == "X" -> Color(0xFF4CAF50) // Green for win
                winner == "O" -> Color(0xFFE53935) // Red for loss
                winner == "Draw" -> Color(0xFF757575) // Gray for draw
                else -> Color(0xFF1E88E5) // Blue for ongoing game
            },
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
                                .clickable(
                                    enabled = isPlayerTurn && board[i][j].isEmpty() && winner == null,
                                    onClick = {
                                        board = board.copyOf().also { it[i][j] = "X" }
                                        checkWinner(board).let {
                                            if (it != null) {
                                                winner = it
                                            } else if (board.all { row -> row.all { it.isNotEmpty() } }) {
                                                winner = "Draw"
                                            } else {
                                                isPlayerTurn = false
                                            }
                                        }
                                    }
                                ),
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
                isPlayerTurn = true
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