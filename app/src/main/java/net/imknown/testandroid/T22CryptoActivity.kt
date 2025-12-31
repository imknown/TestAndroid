package net.imknown.testandroid

import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

class T22CryptoActivity : AppCompatActivity() {
    companion object {
        private const val PROVIDER = "AndroidKeyStore"
        private const val TRANSFORMATION_AES = "AES/GCM/NoPadding"
        private const val TRANSFORMATION_RSA = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    }

    private val oaepParams = OAEPParameterSpec(
        MGF1ParameterSpec.SHA256.digestAlgorithm,
        "MGF1",
        // https://issuetracker.google.com/issues/3670895
        // https://issuetracker.google.com/issues/37075898
        MGF1ParameterSpec.SHA1,
        PSource.PSpecified.DEFAULT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            val rawString = "1234567890".repeat(2)
            val rawBytes = rawString.toByteArray()

            try {
                val secretKey = getOrCreateAesSecretKey("aliasAes")
                val (encryptedBytes, iv) = encryptAes(rawBytes, secretKey)
                val decryptedBytes = decryptAes(encryptedBytes, iv, secretKey)
                println("zzz AES: ${rawString == decryptedBytes.decodeToString()}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val keyPair = getOrCreateRsaKeyPair("aliasRsa")
                val encryptedBytes = encryptRsa(rawBytes, keyPair.public)
                val decryptedBytes = decryptRsa(encryptedBytes, keyPair.private)
                println("zzz RSA: ${rawString == decryptedBytes.decodeToString()}")

                val base64EncryptedString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
                val encryptedBytesDecoded = Base64.decode(base64EncryptedString, Base64.DEFAULT)
                val decryptedBytes2 = decryptRsa(encryptedBytesDecoded, keyPair.private)
                println("zzz RSA Base64: ${rawString == decryptedBytes2.decodeToString()}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // region [AES]
    @Throws
    private fun getOrCreateAesSecretKey(alias: String): SecretKey {
        val keyStore = KeyStore.getInstance(PROVIDER)
        keyStore.load(null)

        if (!keyStore.containsAlias(alias)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER)
            val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val parameterSpec = KeyGenParameterSpec.Builder(alias, purposes)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
            keyGenerator.init(parameterSpec)
            return keyGenerator.generateKey()
        } else {
            return keyStore.getKey(alias, null) as SecretKey
        }
    }

    @Throws
    private fun encryptAes(data: ByteArray, secretKey: SecretKey): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance(TRANSFORMATION_AES)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(data)
        return encryptedData to iv
    }

    @Throws
    private fun decryptAes(encryptedData: ByteArray, iv: ByteArray, secretKey: SecretKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION_AES)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        return cipher.doFinal(encryptedData)
    }
    // endregion [AES]

    // region [RSA]
    @Throws
    private fun getOrCreateRsaKeyPair(alias: String): KeyPair {
        val keyStore = KeyStore.getInstance(PROVIDER).apply {
            load(null)
        }

        if (keyStore.containsAlias(alias)) {
            val privateKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
            return KeyPair(privateKeyEntry.certificate.publicKey, privateKeyEntry.privateKey)
        }

        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, PROVIDER)
        val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(alias, purposes)
            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            .setKeySize(2048)
            .build()
        keyPairGenerator.initialize(keyGenParameterSpec)
        return keyPairGenerator.generateKeyPair()
    }

    @Throws
    private fun encryptRsa(data: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION_RSA)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams)
        return cipher.doFinal(data)
    }

    @Throws
    private fun decryptRsa(encryptedData: ByteArray, privateKey: PrivateKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION_RSA)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher.doFinal(encryptedData)
    }
    // endregion [RSA]
}