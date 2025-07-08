package com.example.demoapp.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.demoapp.blockchain.Blockchain
import com.example.demoapp.crypto.CryptoUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.KeyPair

@Composable
fun SecureChatbotScreen(
    navController: NavController,
    blockchain: Blockchain = Blockchain(),
    userKeyPair: KeyPair = CryptoUtils.generateKeyPair()
) {
    // State declarations
    val safeBlockchain = remember { blockchain }
    val safeKeyPair = remember { userKeyPair }
    val userPublicKey = remember { safeKeyPair.public }

    var userInput by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberLazyListState()

    val chatbotKeyPair = remember { CryptoUtils.generateKeyPair() }
    val chatbotPublicKey = remember { chatbotKeyPair.public }

    // Colors
    val backgroundColor = Color(0xFFF5F5F5)
    val primaryColor = Color(0xFF1E88E5)
    val userBubbleColor = primaryColor.copy(alpha = 0.1f)
    val botBubbleColor = Color.White
    val textColor = Color.Black
    val lightTextColor = Color.Gray

    // Load initial message
    LaunchedEffect(Unit) {
        messages.add(ChatMessage(
            content = "Hello! I'm your HealthChain assistant. How can I help you today?",
            isUser = false,
            isQuickAction = true,
            quickActions = listOf("Book Doctor", "Order Medicine", "Emergency Help")
        ))
        scrollState.animateScrollToItem(messages.size - 1)
    }

    // Function to send a message
    fun sendMessage() {
        if (userInput.isNotBlank()) {
            val inputText = userInput
            userInput = "" // Clear input immediately

            scope.launch {
                try {
                    Log.d("ChatDebug", "Sending message: $inputText")

                    // 1. Immediately show user message in UI
                    val userMessage = "User: $inputText"
                    withContext(Dispatchers.Main) {
                        messages.add(ChatMessage(userMessage, true))
                        scrollState.animateScrollToItem(messages.size - 1)
                    }

                    // 2. Add to blockchain (optional)
                    safeBlockchain.addMessage(userMessage, chatbotPublicKey)

                    // 3. Get bot response (simulated delay for realism)
                    kotlinx.coroutines.delay(500) // Small delay to simulate processing
                    val response = getBotResponse(inputText)
                    val botMessage = "Bot: ${response.message}"
                    Log.d("ChatDebug", "Bot response: ${response.message}")

                    // 4. Add to blockchain (optional)
                    safeBlockchain.addMessage(botMessage, userPublicKey)

                    // 5. Show bot response in UI
                    withContext(Dispatchers.Main) {
                        messages.add(ChatMessage(
                            content = botMessage,
                            isUser = false,
                            isQuickAction = response.quickActions != null,
                            quickActions = response.quickActions ?: emptyList()
                        ))
                        focusManager.clearFocus()
                        scrollState.animateScrollToItem(messages.size - 1)
                    }

                } catch (e: Exception) {
                    Log.e("ChatDebug", "Error sending message", e)
                    withContext(Dispatchers.Main) {
                        messages.add(ChatMessage(
                            content = "Sorry, I encountered an error. Please try again.",
                            isUser = false
                        ))
                        scrollState.animateScrollToItem(messages.size - 1)
                    }
                }
            }
        }
    }

    Scaffold(
        backgroundColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("HealthChain Assistant", color = Color.White) },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                backgroundColor = primaryColor
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            // Chat Messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                state = scrollState
            ) {
                items(messages) { message ->
                    if (message.isQuickAction && message.quickActions?.isNotEmpty() == true) {
                        QuickActionBubble(
                            options = message.quickActions,
                            onOptionSelected = { option ->
                                userInput = option // Set the input to the selected option
                                sendMessage() // Send it immediately
                            },
                            primaryColor = primaryColor,
                            lightTextColor = lightTextColor
                        )
                    } else {
                        ChatBubble(
                            message = message.content,
                            isUser = message.isUser,
                            userBubbleColor = userBubbleColor,
                            botBubbleColor = botBubbleColor,
                            textColor = textColor,
                            lightTextColor = lightTextColor,
                            primaryColor = primaryColor
                        )
                    }
                }
            }

            // Input field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = textColor,
                        cursorColor = primaryColor,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = lightTextColor
                    ),
                    placeholder = {
                        Text("Type a message...", color = lightTextColor)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { sendMessage() }
                    )
                )

                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = { sendMessage() },
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            if (userInput.isNotBlank()) primaryColor else primaryColor.copy(alpha = 0.3f),
                            CircleShape
                        ),
                    enabled = userInput.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: String,
    isUser: Boolean,
    userBubbleColor: Color,
    botBubbleColor: Color,
    textColor: Color,
    lightTextColor: Color,
    primaryColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            backgroundColor = if (isUser) userBubbleColor else botBubbleColor,
            border = BorderStroke(1.dp, if (isUser) primaryColor else lightTextColor.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.substringAfter(": "),
                    color = textColor,
                    fontSize = 16.sp
                )
                Text(
                    text = if (isUser) "You" else "HealthBot",
                    color = lightTextColor,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickActionBubble(
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    primaryColor: Color,
    lightTextColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Quick options:",
            color = lightTextColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.Start),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                Button(
                    onClick = { onOptionSelected(option) },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = primaryColor.copy(alpha = 0.1f),
                        contentColor = primaryColor
                    ),
                    border = BorderStroke(1.dp, primaryColor),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(option)
                }
            }
        }
    }
}

private data class ChatMessage(
    val content: String,
    val isUser: Boolean,
    val isQuickAction: Boolean = false,
    val quickActions: List<String>? = null
)

private fun getBotResponse(input: String): BotResponse {
    return when {
        input.contains("hello", ignoreCase = true) ||
                input.contains("hi", ignoreCase = true) ->
            BotResponse(
                "Hello! I'm your HealthChain assistant. How can I help you today?",
                listOf("Book Appointment", "Medicine Query", "Emergency Help")
            )

        input.contains("appointment", ignoreCase = true) ||
                input.contains("book doctor", ignoreCase = true) ||
                input.contains("doctor", ignoreCase = true) ->
            BotResponse(
                "I can help you book an appointment. Which specialist do you need?",
                listOf("General Physician", "Cardiologist", "Dermatologist")
            )

        input.contains("medicine", ignoreCase = true) ||
                input.contains("prescription", ignoreCase = true) ||
                input.contains("order medicine", ignoreCase = true) ->
            BotResponse(
                "For medicines, please provide the names or upload a prescription:",
                listOf("Upload Prescription", "Search Medicines")
            )

        input.contains("emergency", ignoreCase = true) ||
                input.contains("help", ignoreCase = true) ->
            BotResponse(
                "EMERGENCY SERVICES ACTIVATED. We've alerted nearby hospitals.",
                listOf("Call Ambulance", "First Aid Guide")
            )

        input.contains("general physician", ignoreCase = true) ->
            BotResponse(
                "I've found available General Physicians near you. Would you like to book an appointment?",
                listOf("Book Now", "See Availability", "Cancel")
            )

        input.contains("cardiologist", ignoreCase = true) ->
            BotResponse(
                "Cardiology specialists are available. Please specify your preferred time:",
                listOf("Morning", "Afternoon", "Evening")
            )

        input.contains("dermatologist", ignoreCase = true) ->
            BotResponse(
                "Dermatology appointments can be booked. Would you like to proceed?",
                listOf("Yes, Book Now", "No, Thanks")
            )

        else ->
            BotResponse(
                "I'm here to assist with your healthcare needs. You can ask about appointments, medicines, or emergencies.",
                listOf("Book Doctor", "Order Medicine", "Emergency")
            )
    }
}

private data class BotResponse(
    val message: String,
    val quickActions: List<String>?
)