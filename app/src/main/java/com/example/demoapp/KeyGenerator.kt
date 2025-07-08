package com.example.demoapp.utils

import org.web3j.crypto.Keys
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.utils.Numeric

fun generatePrivateKey(): String {
    // Generate a random ECKeyPair (private and public key)
    val keyPair = ECKeyPair.create(Keys.createEcKeyPair().privateKey)
    val privateKey = keyPair.privateKey
    return Numeric.toHexStringWithPrefix(privateKey)
}
