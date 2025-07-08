package com.example.demoapp

import android.content.Context
import android.util.Log
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.TransactionManager
import org.web3j.tx.response.PollingTransactionReceiptProcessor
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Utf8String
import java.math.BigInteger

class BlockchainHelper(private val context: Context) {

    private val web3j: Web3j = Web3j.build(HttpService("https://mainnet.infura.io/v3/d9c2983e96d6440390d659f12896c839"))
    private val contractAddress = "0xd9145CCE52D386f254917e481eB44e9943F39138" // Replace with your contract address
    private val credentials: Credentials = Credentials.create("4c0883a69102937d6231471b5dbb6204fe51296170827907715fd2b1cb89cfec") // Replace with your private key

    // Gas price and gas limit
    private val gasPrice: BigInteger = BigInteger.valueOf(20_000_000_000L) // 20 Gwei
    private val gasLimit: BigInteger = BigInteger.valueOf(300_000L) // 300,000 gas

    init {
        Log.d("BlockchainHelper", "Initialized Web3j and Wallet credentials")
    }

    /**
     * Load the ABI from the assets folder.
     * @return The ABI as a string.
     */
    fun getAbi(): String {
        return try {
            val abiFile = context.assets.open("scenario.json") // Ensure the file is in the assets folder
            abiFile.bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            Log.e("BlockchainHelper", "Error loading ABI: ${e.message}")
            throw e
        }
    }

    /**
     * Store user data (name and email) on the blockchain.
     */
    fun storeUserData(name: String, email: String): TransactionReceipt? {
        try {
            val function = Function(
                "storeUserData",
                listOf(Utf8String(name), Utf8String(email)),
                emptyList()
            )
            val encodedFunction = FunctionEncoder.encode(function)

            val transactionManager: TransactionManager = RawTransactionManager(web3j, credentials)
            val transactionResponse: EthSendTransaction = transactionManager.sendTransaction(
                gasPrice,
                gasLimit,
                contractAddress,
                encodedFunction,
                BigInteger.ZERO
            )

            Log.d("BlockchainHelper", "Transaction Hash: ${transactionResponse.transactionHash}")

            val receiptProcessor = PollingTransactionReceiptProcessor(web3j, 1000, 15)
            return receiptProcessor.waitForTransactionReceipt(transactionResponse.transactionHash)
        } catch (e: Exception) {
            Log.e("BlockchainHelper", "Error storing user data: ${e.message}")
            return null
        }
    }

    /**
     * Retrieve user data (name and email) from the blockchain.
     */
    fun getUserData(): Pair<String, String>? {
        try {
            val function = Function(
                "getUserData",
                emptyList(),
                listOf(
                    object : TypeReference<Utf8String>() {},
                    object : TypeReference<Utf8String>() {}
                )
            )
            val encodedFunction = FunctionEncoder.encode(function)

            val ethCall = web3j.ethCall(
                Transaction.createEthCallTransaction(
                    credentials.address,
                    contractAddress,
                    encodedFunction
                ),
                DefaultBlockParameterName.LATEST
            ).send()

            val output = FunctionReturnDecoder.decode(ethCall.value, function.outputParameters)
            if (output.size >= 2) {
                val name = output[0].value as String
                val email = output[1].value as String
                return Pair(name, email)
            }
        } catch (e: Exception) {
            Log.e("BlockchainHelper", "Error retrieving user data: ${e.message}")
        }

        return null
    }
}
