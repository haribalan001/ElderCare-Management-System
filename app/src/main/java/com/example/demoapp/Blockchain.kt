package com.example.demoapp.blockchain

import com.example.demoapp.crypto.CryptoUtils
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*

data class Block(
    val index: Int,
    val timestamp: Long,
    val data: String,
    val previousHash: String,
    val nonce: Int = 0,
    val signature: String = ""
) {
    val hash: String
        get() = CryptoUtils.hash("$index$timestamp$data$previousHash$nonce")
}

class Blockchain {
    private val chain = mutableListOf<Block>()
    private val pendingMessages = mutableListOf<String>()
    private val miningDifficulty = 4
    private val prefix = "0".repeat(miningDifficulty)

    init {
        createGenesisBlock()
    }

    private fun createGenesisBlock() {
        val genesisBlock = Block(
            index = 0,
            timestamp = System.currentTimeMillis(),
            data = "Genesis Block",
            previousHash = "0"
        )
        chain.add(genesisBlock)
    }

    fun addMessage(message: String, publicKey: PublicKey): Block {
        val encryptedMessage = CryptoUtils.encrypt(message, publicKey)
        pendingMessages.add(encryptedMessage)
        return mineBlock(encryptedMessage)
    }

    private fun mineBlock(data: String): Block {
        val lastBlock = chain.last()
        val newIndex = lastBlock.index + 1
        var nonce = 0
        lateinit var newHash: String
        lateinit var newBlock: Block

        do {
            newBlock = Block(
                index = newIndex,
                timestamp = System.currentTimeMillis(),
                data = data,
                previousHash = lastBlock.hash,
                nonce = nonce++
            )
            newHash = newBlock.hash
        } while (!newHash.startsWith(prefix))

        chain.add(newBlock)
        pendingMessages.clear()
        return newBlock
    }

    // Updated to require private key for decryption
    fun getChatHistory(privateKey: PrivateKey): List<String> {
        return chain.drop(1) // Skip genesis block
            .map { block ->
                try {
                    CryptoUtils.decrypt(block.data, privateKey)
                } catch (e: Exception) {
                    "🔒 [Decryption Error]"
                }
            }
    }

    fun validateChain(): Boolean {
        for (i in 1 until chain.size) {
            val current = chain[i]
            val previous = chain[i - 1]

            if (current.hash != current.hash) return false
            if (current.previousHash != previous.hash) return false
            if (!current.hash.startsWith(prefix)) return false
        }
        return true
    }
}