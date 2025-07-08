package com.example.demoapp.crypto

import android.util.Base64
import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.text.Charsets.UTF_8

object CryptoUtils {
    fun generateKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2048)
        return keyGen.generateKeyPair()
    }

    fun encrypt(message: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val encryptedBytes = cipher.doFinal(message.toByteArray(UTF_8))
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    fun decrypt(encryptedMessage: String, privateKey: PrivateKey): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val encryptedBytes = Base64.decode(encryptedMessage, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, UTF_8)
    }

    fun hash(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(UTF_8))
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }
    // Add these to your CryptoUtils if you don't have them
    fun convertPublicKeyToString(publicKey: PublicKey): String {
        return Base64.encodeToString(publicKey.encoded, Base64.DEFAULT)
    }

    fun convertStringToPublicKey(encodedString: String): PublicKey {
        val keyBytes = Base64.decode(encodedString, Base64.DEFAULT)
        return KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(keyBytes))
    }
    fun generateAESKey(): SecretKeySpec {
        val key = ByteArray(16).also {
            SecureRandom().nextBytes(it)
        }
        return SecretKeySpec(key, "AES")
    }
}