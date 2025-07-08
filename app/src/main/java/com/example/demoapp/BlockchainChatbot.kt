package com.example.demoapp.screens

import java.util.concurrent.ConcurrentHashMap

class BlockchainChatbot {
    private val blockchain = ConcurrentHashMap<Int, String>()
    private var currentIndex = 0

    init {
        // Preload some blockchain-based responses
        blockchain[currentIndex++] = "Hello! How can I assist you?"
        blockchain[currentIndex++] = "I'm a blockchain-based chatbot! Ask me anything."
        blockchain[currentIndex++] = "Your messages are securely stored on a local blockchain!"
    }

    fun getResponse(userMessage: String): String {
        val responseIndex = userMessage.length % blockchain.size
        return blockchain[responseIndex] ?: "I'm learning, please ask again!"
    }
}
