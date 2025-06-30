package net.imknown.testandroid

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import net.imknown.testandroid.databinding.T10ActivityBiometricsBinding
import net.imknown.testandroid.ext.zLog

class T10BiometricsActivity : AppCompatActivity() {

    companion object {
        private const val AUTHENTICATORS = BiometricManager.Authenticators.BIOMETRIC_STRONG
    }

    private val binding by lazy { T10ActivityBiometricsBinding.inflate(layoutInflater) }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        zLog("Fingerprint enrolling ${
            when (result.resultCode) {
                RESULT_CANCELED ->
                    "canceled"
                else ->
                    "successfully"
            }
        }. (${result.resultCode})")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
    }

    @Suppress("UNUSED_PARAMETER")
    fun getStatus(view: View) {
        when (BiometricManager.from(this).canAuthenticate(AUTHENTICATORS)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                zLog("BIOMETRIC_SUCCESS")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                zLog("BIOMETRIC_ERROR_NO_HARDWARE")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                zLog("BIOMETRIC_ERROR_HW_UNAVAILABLE")
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                zLog("BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED")
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                zLog("BIOMETRIC_ERROR_UNSUPPORTED")
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                zLog("BIOMETRIC_STATUS_UNKNOWN")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                zLog("BIOMETRIC_ERROR_NONE_ENROLLED")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // Go to the fingerprint enrolling.
                    // But why fingerprint, not face or iris?
                    // Also it seems like this is supported in Android Q, and it's weird.
                    launcher.launch(Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            AUTHENTICATORS
                        )
                    })
                } else {
                    // Go to the security center
                    startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun showPrompt(view: View) {
        val biometricPrompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence,
                ) {
                    zLog("onAuthenticationError(): $errString")
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult,
                ) {
                    zLog("onAuthenticationSucceeded(): ${result.authenticationType}, ${result.cryptoObject?.cipher}")
                }

                override fun onAuthenticationFailed() {
                    zLog("onAuthenticationFailed")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.BiometricPromptTitle))
            .setSubtitle(getString(R.string.BiometricPromptSubtitle))
            .setNegativeButtonText(getString(R.string.BiometricPromptNegativeButtonText))
            .setDescription(getString(R.string.BiometricPromptDescription))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}