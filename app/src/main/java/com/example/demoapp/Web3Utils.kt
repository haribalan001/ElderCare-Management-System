

package com.example.demoapp.utils

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

object Web3Utils {
    fun checkWeb3Connection() {
        val web3 = Web3j.build(HttpService("http://10.0.2.2:8545")) // Replace with actual local IP

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val web3ClientVersion = web3.web3ClientVersion().send()
                Log.d("Web3", "Connected to Ethereum Node: ${web3ClientVersion.web3ClientVersion}")
            } catch (e: Exception) {
                Log.e("Web3", "Failed to connect: ${e.message}")
            }
        }
    }
}
