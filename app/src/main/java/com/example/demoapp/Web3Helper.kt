package com.example.demoapp.util

import android.util.Log
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.Utf8String
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger

private const val TAG = "Web3Helper"

object Web3Helper {
    private const val INFURA_URL = "http://10.0.2.2:7545"
    private const val PRIVATE_KEY = "0xeaa23291ee03debde66d6322a0cf540f3527ffb64de2dfbbd692dcae3ca74e78"
    private const val CONTRACT_ADDRESS = "0xD7ACd2a9FD159E69Bb102A1ca21C9a3e3A5F771B"

    val web3j: Web3j by lazy { Web3j.build(HttpService(INFURA_URL)) }
    val credentials: Credentials by lazy { Credentials.create(PRIVATE_KEY) }
    private val transactionManager: RawTransactionManager by lazy { RawTransactionManager(web3j, credentials) }

    fun isConnected(): Boolean {
        return try {
            val clientVersion = web3j.web3ClientVersion().send()
            if (!clientVersion.hasError()) {
                Log.d(TAG, "✅ Connected to Ethereum node: ${clientVersion.web3ClientVersion}")
                true
            } else {
                Log.e(TAG, "❌ Error connecting: ${clientVersion.error.message}")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Connection failed: ${e.message}", e)
            false
        }
    }

    suspend fun assignCaregiverOnBlockchain(
        caregiverName: String,
        patientName: String,
        shiftStart: String,
        shiftEnd: String
    ): Boolean {
        return try {
            val gasPrice = BigInteger.valueOf(20000000000)
            val gasLimit = BigInteger.valueOf(6000000)

            val functionData = encodeFunctionData(caregiverName, patientName, shiftStart, shiftEnd)

            val transaction = transactionManager.sendTransaction(
                gasPrice,
                gasLimit,
                CONTRACT_ADDRESS,
                functionData,
                BigInteger.ZERO
            )

            if (transaction.transactionHash.isNotEmpty()) {
                Log.d(TAG, "✅ Caregiver assigned! TxHash: ${transaction.transactionHash}")
                true
            } else {
                Log.e(TAG, "❌ Transaction failed!")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Transaction error: ${e.message}", e)
            false
        }
    }

    /**
     * Properly encodes function call for the smart contract.
     */
    private fun encodeFunctionData(
        caregiverName: String,
        patientName: String,
        shiftStart: String,
        shiftEnd: String
    ): String {
        val function = Function(
            "assignCaregiver",
            listOf<Type<*>>(
                Utf8String(caregiverName),
                Utf8String(patientName),
                Utf8String(shiftStart),
                Utf8String(shiftEnd)
            ),
            emptyList()
        )
        return FunctionEncoder.encode(function)
    }
}
