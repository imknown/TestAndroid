package net.imknown.testandroid

import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.autofill.AutofillManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import net.imknown.testandroid.ext.zLog

class T25FidoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        printPasskeys()
    }

    private fun printPasskeys() {
        val activity = this
        val keyguardManager = activity.getSystemService<KeyguardManager>()
        if (keyguardManager == null) {
            zLog("KeyguardManager not support")
            return
        }

        val isDeviceSecure = keyguardManager.isDeviceSecure
        zLog("isDeviceSecure: $isDeviceSecure")
        if (!isDeviceSecure) {
            val intentSecuritySettings = Intent(Settings.ACTION_SECURITY_SETTINGS)
            activity.startActivity(intentSecuritySettings)
            return
        }

        fun intent(action: String) {
            val intentAutofill = Intent(action, "package: ${activity.packageName}".toUri())
            activity.startActivity(intentAutofill)
        }

        val credentialServices = Settings.Secure.getString(activity.contentResolver, "credential_service")
        val credentialServicePrimary = Settings.Secure.getString(activity.contentResolver, "credential_service_primary")
        zLog("credential_service: $credentialServices")
        zLog("credential_service_primary: $credentialServicePrimary")
        if (credentialServicePrimary != null) {
            return
        }

        // https://developer.android.com/identity/sign-in/credential-provider#settings-intents
        // https://developer.android.com/identity/sign-in/credential-provider#enable-providers
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            intent(Settings.ACTION_CREDENTIAL_PROVIDER)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            intent("android.settings.CREDENTIAL_PROVIDER")
            return
        }

        val autofillService = Settings.Secure.getString(activity.contentResolver, "autofill_service")
        zLog("autofill_service: $autofillService")
        if (autofillService != null) {
            return
        }

        val autofillManager = activity.getSystemService<AutofillManager>()
        if (autofillManager == null) {
            zLog("AutofillManager not support")
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val isEnabled = autofillManager.isEnabled
            val hasEnabledAutofillServices = autofillManager.hasEnabledAutofillServices()
            val isAutofillSupported = autofillManager.isAutofillSupported()
            zLog("isEnabled: $isEnabled")
            zLog("hasEnabledAutofillServices: $hasEnabledAutofillServices")
            zLog("isAutofillSupported: $isAutofillSupported")

            intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
        }
    }
}