package net.imknown.testandroid

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.browser.auth.AuthTabCallback
import androidx.browser.auth.AuthTabColorSchemeParams
import androidx.browser.auth.AuthTabIntent
import androidx.browser.auth.AuthTabIntent.AuthResult
import androidx.browser.auth.AuthTabSession
import androidx.browser.customtabs.CustomTabsCallback.NAVIGATION_ABORTED
import androidx.browser.customtabs.CustomTabsCallback.NAVIGATION_FAILED
import androidx.browser.customtabs.CustomTabsCallback.NAVIGATION_FINISHED
import androidx.browser.customtabs.CustomTabsCallback.NAVIGATION_STARTED
import androidx.browser.customtabs.CustomTabsCallback.TAB_HIDDEN
import androidx.browser.customtabs.CustomTabsCallback.TAB_SHOWN
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.net.toUri
import net.imknown.testandroid.ext.zLog

class T24AuthTabServiceConnection(
    private val context: Context,
    private val url: String,
    private val packageNameCt: String,
    private val launcher: ActivityResultLauncher<Intent>?
): CustomTabsServiceConnection() {
    companion object {
        fun initAuthTabLauncher(caller: ComponentActivity, onOk: (Uri?) -> Unit): ActivityResultLauncher<Intent> {
            val callback = ActivityResultCallback<AuthResult> { result ->
                val resultCode = result.resultCode
                var message = when (resultCode) {
                    AuthTabIntent.RESULT_OK -> "Received auth result."
                    AuthTabIntent.RESULT_CANCELED -> "AT canceled."
                    AuthTabIntent.RESULT_VERIFICATION_FAILED -> "Verification failed."
                    AuthTabIntent.RESULT_VERIFICATION_TIMED_OUT -> "Verification timed out."
                    else -> "Unexpected value: $resultCode"
                }

                if (resultCode == AuthTabIntent.RESULT_OK) {
                    val uri = result.resultUri
                    message += " Uri: $uri"
                    onOk(uri)
                }

                zLog("handleAuthResult: $message")
            }
            return AuthTabIntent.registerActivityResultLauncher(caller, callback)
        }
    }

    private val tag = T24AuthTabServiceConnection::class.simpleName

    private var customTabsClient: CustomTabsClient? = null
    private var authTabSession: AuthTabSession? = null

    override fun onCustomTabsServiceConnected(name: ComponentName, customTabsClient: CustomTabsClient) {
        zLog("$tag: onCustomTabsServiceConnected: $name")

        this.customTabsClient = customTabsClient

        val authTabCallback = object: AuthTabCallback {
            private val tag = AuthTabCallback::class.simpleName

            override fun onWarmupCompleted(extras: Bundle) {
                zLog("$tag: onWarmupCompleted: $extras")
            }

            override fun onExtraCallback(callbackName: String, args: Bundle) {
                zLog("$tag: extraCallback: $callbackName, $args")
            }

            override fun onExtraCallbackWithResult(callbackName: String, args: Bundle): Bundle {
                zLog("$tag: onExtraCallbackWithResult: $callbackName, $args")
                return Bundle.EMPTY
            }

            override fun onNavigationEvent(navigationEvent: Int, extras: Bundle) {
                val event: String? = when (navigationEvent) {
                    NAVIGATION_ABORTED -> "NAVIGATION_ABORTED"
                    NAVIGATION_FAILED -> "NAVIGATION_FAILED"
                    NAVIGATION_FINISHED -> "NAVIGATION_FINISHED"
                    NAVIGATION_STARTED -> "NAVIGATION_STARTED"
                    TAB_SHOWN -> "TAB_SHOWN"
                    TAB_HIDDEN -> "TAB_HIDDEN"
                    else -> navigationEvent.toString()
                }
                zLog("$tag: onNavigationEvent: $event, $extras")
            }
        }
        val authTabSession = customTabsClient.newAuthTabSession(authTabCallback, null)

        this.authTabSession = authTabSession

        if (authTabSession == null) {
            zLog("$tag: authTabSession is null")
        }

        val customTabsSession = customTabsClient.newSession(null)

        // this.customTabsSession = customTabsSession

        if (customTabsSession == null) {
            zLog("$tag: customTabsSession is null")
        }

        customTabsClient.warmup(0)

        val validated = customTabsSession?.validateRelationship(
            CustomTabsService.RELATION_USE_AS_ORIGIN, url.toUri(), null
        )
        zLog("$tag: validateRelationship: $validated")

        val isLaunched = customTabsSession?.mayLaunchUrl(url.toUri(), null, null)
        zLog("$tag: mayLaunchUrl: $isLaunched")

        T24EngagementSignalsCallback().setup(customTabsSession)

        val light = AuthTabColorSchemeParams.Builder()
            // .setToolbarColor(Color.RED)
            .build()
        val dark = AuthTabColorSchemeParams.Builder()
            // .setToolbarColor(Color.GREEN)
            .build()
        val authTabIntent: AuthTabIntent = AuthTabIntent.Builder()
            .setDefaultColorSchemeParams(light)
            .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, dark)
            // .setCloseButtonIcon(Bitmap)
            .setEphemeralBrowsingEnabled(CustomTabsClient.isEphemeralBrowsingSupported(context, packageNameCt))
            .apply {
                if (authTabSession != null) {
                    setSession(authTabSession)
                }
            }
            .build()
        val intent = authTabIntent.intent
        intent.putExtra(Intent.EXTRA_REFERRER, "android-app://${context.packageName}".toUri())

        val launcher = launcher ?: return
        authTabIntent.launch(launcher, url.toUri(), "auth", "/callback")
//        authTabIntent.launch(launcher, url.toUri(), "sbikabu2")
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        zLog("$tag: onServiceDisconnected: $name")
        destroy()
    }

    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
        zLog("$tag: onBindingDied: $name")
        destroy()
    }

    override fun onNullBinding(name: ComponentName?) {
        super.onNullBinding(name)
        zLog("$tag: onNullBinding: $name")
        destroy()
    }

    fun destroy() {
        customTabsClient = null
        authTabSession = null
    }
}