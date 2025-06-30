package net.imknown.testandroid

import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class T22CryptoActivity : AppCompatActivity() {
    companion object {
        private const val PROVIDER = "AndroidKeyStore"
        private const val TRANSFORMATION_AES = "AES/GCM/NoPadding"
        private const val TRANSFORMATION_RSA = "RSA/ECB/PKCS1Padding"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val rawString = "1234567890".repeat(30)
            val rawBytes = rawString.toByteArray()

            try {
                val secretKey = getOrCreateAesSecretKey("aliasAes")
                val (encryptedBytes, iv) = encryptAes(rawBytes, secretKey)
                val decryptedBytes = decryptAes(encryptedBytes, iv, secretKey)
                println("zzz AES: ${rawString == String(decryptedBytes)}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val keyPair = getOrCreateRsaKeyPair("aliasRsa")
                val encryptedBytes = encryptRsa(rawBytes, keyPair.public)
                val decryptedBytes = decryptRsa(encryptedBytes, keyPair.private)
                println("zzz RSA: ${rawString == String(decryptedBytes)}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
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
    @RequiresApi(Build.VERSION_CODES.M)
    @Throws
    private fun getOrCreateRsaKeyPair(alias: String): KeyPair {
        val keyStore = KeyStore.getInstance(PROVIDER)
        keyStore.load(null)

        if (!keyStore.containsAlias(alias)) {
            val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, PROVIDER)
            val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(alias, purposes)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build()
            keyPairGenerator.initialize(keyGenParameterSpec)
            return keyPairGenerator.generateKeyPair()
        } else {
            val privateKeyEntry = keyStore.getEntry(alias, null) as KeyStore.PrivateKeyEntry
            return KeyPair(privateKeyEntry.certificate.publicKey, privateKeyEntry.privateKey)
        }
    }

    @Throws
    private fun encryptRsa(data: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION_RSA)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
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