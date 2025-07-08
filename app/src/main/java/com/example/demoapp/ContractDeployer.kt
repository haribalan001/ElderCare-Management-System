package com.example.demoapp.utils

import android.util.Log
import com.example.demoapp.ElderCareContract
import org.web3j.protocol.Web3j
import org.web3j.crypto.Credentials
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger

class ContractDeployer(private val web3: Web3j, private val credentials: Credentials) {

    fun deployContract(): String {
        return try {
            Log.d("ContractDeployer", "🚀 Deploying contract...")
            val contract = ElderCareContract.deploy(
                web3,
                credentials,
                DefaultGasProvider()
            ).send()
            Log.d("ContractDeployer", "✅ Contract deployed at: ${contract.contractAddress}")
            contract.contractAddress
        } catch (e: Exception) {
            Log.e("ContractDeployer", "❌ Contract deployment failed", e)
            e.printStackTrace()  // Prints error details in Logcat
            ""
        }
    }

}

