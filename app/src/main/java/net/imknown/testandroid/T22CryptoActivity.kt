package net.imknown.testandroid

import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.imknown.testandroid.ext.zLog
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.AlgorithmParameterSpec
import java.security.spec.MGF1ParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import javax.crypto.spec.SecretKeySpec

class T22CryptoActivity : AppCompatActivity() {
    companion object {
        private const val PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val TRANSFORMATION_AES = "AES/GCM/NoPadding"
        private const val TRANSFORMATION_RSA = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
        private const val MGF1 = "MGF1"
    }

    private val keyStoreOrThrow = KeyStore.getInstance(PROVIDER_ANDROID_KEYSTORE).apply {
        load(null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {
            val aliasAes = "aliasAes"
            val rawAesString = "1234567890".repeat(20)
            val rawAesBytes = rawAesString.toByteArray()
            zLog("AES Raw: $rawAesString")

            try {
                val aesSecretKey = getOrCreateAesSecretKeyOrThrow(aliasAes)
                val dataAndIv = encryptAesOrThrow(aesSecretKey, rawAesBytes)
                val decryptedBytes = decryptAesOrThrow(aesSecretKey, dataAndIv)
                zLog("AES: ${decryptedBytes.decodeToString()}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val aliasRsa = "aliasRsa"
            // 2048-bit RSA Key (SHA-256):
            // - OAEP Padding:
            // 256 bytes (key size) - 2 * (32 bytes for SHA-256) - 2 = 190 bytes.
            // - PKCS#1 v1.5 padding:
            // 256 bytes (key size) - 11 bytes = 245 bytes.
            val rawRsaString = "1234567890".repeat(19)
            val rawRsaBytes = rawRsaString.toByteArray()
            zLog("RSA Raw: $rawRsaString")

            try {
                val rsaKeyPair = getOrCreateRsaKeyPairUsingAndroidKeyStoreOrThrow(aliasRsa)
                val encryptedBytes = encryptRsaOrThrow(rsaKeyPair.public, rawRsaBytes)
                val decryptedBytes = decryptRsaOrThrow(rsaKeyPair.private, encryptedBytes)
                zLog("RSA-OAEP: ${decryptedBytes.decodeToString()}")

                val base64EncryptedString = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
                val encryptedBytesDecoded = Base64.decode(base64EncryptedString, Base64.NO_WRAP)
                val decryptedBytes2 = decryptRsaOrThrow(rsaKeyPair.private, encryptedBytesDecoded)
                zLog("RSA-OAEP (Base64): ${decryptedBytes2.decodeToString()}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val rsaKeyPair = getOrCreateRsaKeyPairUsingAndroidKeyStoreOrThrow(aliasRsa)
                val payload = encryptRsaAesOrThrow(rsaKeyPair, rawAesBytes) // Save it to the local file, or send it over the network
                val decryptedBytes = decryptRsaAesOrThrow(rsaKeyPair, payload)
                zLog("RSA-KEM (Base64 AES): ${decryptedBytes.decodeToString()}")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val rsaKeyPairInMemery = createRsaKeyPairInMemeryOrThrow()
                val encryptedBytes = encryptRsaMgf1Sha256OrThrow(rsaKeyPairInMemery.public, rawRsaBytes)
                val decryptedBytes = decryptRsaMgf1Sha256OrThrow(rsaKeyPairInMemery.private, encryptedBytes)
                zLog("RSA-OAEP (MGF1-SHA256): ${decryptedBytes.decodeToString()}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private class DataAndIv(val data: ByteArray, val iv: ByteArray?)

    private fun cipherOrThrow(
        transformation: String, opmode: Int, key: Key, params: AlgorithmParameterSpec?,
        data: ByteArray
    ): DataAndIv {
        val cipher = Cipher.getInstance(transformation)
        if (params != null) {
            cipher.init(opmode, key, params)
        } else {
            cipher.init(opmode, key)
        }
        val iv = cipher.iv
        return DataAndIv(cipher.doFinal(data), iv)
    }

    // region [AES]
    private fun getOrCreateAesSecretKeyOrThrow(alias: String): SecretKey {
        if (!keyStoreOrThrow.containsAlias(alias)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER_ANDROID_KEYSTORE)
            val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val parameterSpec = KeyGenParameterSpec.Builder(alias, purposes)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
            keyGenerator.init(parameterSpec)
            return keyGenerator.generateKey()
        } else {
            return keyStoreOrThrow.getKey(alias, null) as SecretKey
        }
    }

    private fun encryptAesOrThrow(secretKey: SecretKey, data: ByteArray): DataAndIv {
        return cipherOrThrow(TRANSFORMATION_AES, Cipher.ENCRYPT_MODE, secretKey, null, data)
    }

    private fun decryptAesOrThrow(secretKey: SecretKey, dataAndIv: DataAndIv): ByteArray {
        val spec = GCMParameterSpec(128, dataAndIv.iv)
        return cipherOrThrow(TRANSFORMATION_AES, Cipher.DECRYPT_MODE, secretKey, spec, dataAndIv.data).data
    }
    // endregion [AES]

    // region [RSA]
    private fun getOrCreateRsaKeyPairUsingAndroidKeyStoreOrThrow(alias: String): KeyPair {
        if (keyStoreOrThrow.containsAlias(alias)) {
            val privateKeyEntry = keyStoreOrThrow.getEntry(alias, null) as KeyStore.PrivateKeyEntry
            return KeyPair(privateKeyEntry.certificate.publicKey, privateKeyEntry.privateKey)
        }

        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_RSA, PROVIDER_ANDROID_KEYSTORE
        )
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

    private val oaepParamsMdSha256Mgf1Sha1 = OAEPParameterSpec(
        MGF1ParameterSpec.SHA256.digestAlgorithm,
        MGF1,
        // https://issuetracker.google.com/issues/3670895
        // https://issuetracker.google.com/issues/37075898
        MGF1ParameterSpec.SHA1,
        PSource.PSpecified.DEFAULT
    )

    private fun encryptRsaOrThrow(publicKey: PublicKey, data: ByteArray): ByteArray {
        return cipherOrThrow(TRANSFORMATION_RSA, Cipher.ENCRYPT_MODE, publicKey, oaepParamsMdSha256Mgf1Sha1, data).data
    }

    private fun decryptRsaOrThrow(privateKey: PrivateKey, data: ByteArray): ByteArray {
        return cipherOrThrow(TRANSFORMATION_RSA, Cipher.DECRYPT_MODE, privateKey, oaepParamsMdSha256Mgf1Sha1, data).data
    }

    private fun createRsaKeyPairInMemeryOrThrow(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.generateKeyPair()
    }

    private val oaepParamsMdSha256Mgf1Sha256 = OAEPParameterSpec(
        MGF1ParameterSpec.SHA256.digestAlgorithm,
        MGF1,
        MGF1ParameterSpec.SHA256,
        PSource.PSpecified.DEFAULT
    )

    private fun encryptRsaMgf1Sha256OrThrow(publicKey: PublicKey, data: ByteArray): ByteArray {
        return cipherOrThrow(TRANSFORMATION_RSA, Cipher.ENCRYPT_MODE, publicKey, oaepParamsMdSha256Mgf1Sha256, data).data
    }

    private fun decryptRsaMgf1Sha256OrThrow(privateKey: PrivateKey, data: ByteArray): ByteArray {
        return cipherOrThrow(TRANSFORMATION_RSA, Cipher.DECRYPT_MODE, privateKey, oaepParamsMdSha256Mgf1Sha256, data).data
    }
    // endregion [RSA]

    // region [RSA-KEM]
    private data class EncryptedPayload(
        val encryptedAesKeyBase64: String,
        val encryptedDataBase64: String,
        val ivBase64: String
    )

    private fun encryptRsaAesOrThrow(rsaKeyPair: KeyPair, rawBytes: ByteArray): EncryptedPayload {
        val aesSecretKey = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES).run {
            init(256)
            generateKey()
        }

        // Use the RSA PublicKey to encrypt the AES key
        val encryptedAesKey = encryptRsaOrThrow(rsaKeyPair.public, aesSecretKey.encoded)

        // Use the AES key to encrypt the original data
        val dataAndIv = encryptAesOrThrow(aesSecretKey, rawBytes)

        return EncryptedPayload(
            encryptedAesKeyBase64 = Base64.encodeToString(encryptedAesKey, Base64.NO_WRAP),
            encryptedDataBase64 = Base64.encodeToString(dataAndIv.data, Base64.NO_WRAP),
            ivBase64 = Base64.encodeToString(dataAndIv.iv, Base64.NO_WRAP)
        )
    }

    private fun decryptRsaAesOrThrow(rsaKeyPair: KeyPair, payload: EncryptedPayload): ByteArray {
        val encryptedAesKey = Base64.decode(payload.encryptedAesKeyBase64, Base64.NO_WRAP)

        // Use the RSA PrivateKey to decrypt the AES key
        val decryptedAesKeyBytes = decryptRsaOrThrow(rsaKeyPair.private, encryptedAesKey)
        val aesSecretKey = SecretKeySpec(decryptedAesKeyBytes, KeyProperties.KEY_ALGORITHM_AES)

        // Use the decrypted AES key and the IV to decrypt
        val iv = Base64.decode(payload.ivBase64, Base64.NO_WRAP)
        val encryptedData = Base64.decode(payload.encryptedDataBase64, Base64.NO_WRAP)
        return decryptAesOrThrow(aesSecretKey, DataAndIv(encryptedData, iv))
    }
    // endregion [RSA-KEM]

    private fun testBase64(encoded: ByteArray) {
        val result = mutableListOf<String>()
        val android = Base64.encodeToString(encoded, Base64.URL_SAFE or Base64.NO_WRAP)
        result += "Android: $android"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val java = java.util.Base64.getUrlEncoder().encodeToString(encoded)
            result += "Java: $java"
        }
        val kotlin = kotlin.io.encoding.Base64.UrlSafe.encode(encoded)
        result += "Kotlin: $kotlin"

        zLog(result.joinToString("\n"))
    }
}