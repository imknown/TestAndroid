package net.imknown.testandroid

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.browser.customtabs.EngagementSignalsCallback
import androidx.core.net.toUri
import net.imknown.testandroid.ext.zLog
import java.util.Locale

class T24CustomTabsServiceConnection(
    private val context: Context,
    private val url: String,
    private val packageNameCt: String
): CustomTabsServiceConnection() {
    private val tag = CustomTabsServiceConnection::class.simpleName

    private var customTabsClient: CustomTabsClient? = null
    private var customTabsSession: CustomTabsSession? = null

    override fun onCustomTabsServiceConnected(name: ComponentName, customTabsClient: CustomTabsClient) {
        zLog("$tag: onCustomTabsServiceConnected: $name")

        this.customTabsClient = customTabsClient

        val customTabsSession = customTabsClient.newSession(T24CustomTabsCallback())

        this.customTabsSession = customTabsSession

        if (customTabsSession == null) {
            zLog("$tag: customTabsSession == null")
        }

        customTabsClient.warmup(0)

        val validated = customTabsSession?.validateRelationship(
            CustomTabsService.RELATION_USE_AS_ORIGIN, url.toUri(), null
        )
        zLog("$tag: validateRelationship: $validated")

        val isLaunched = customTabsSession?.mayLaunchUrl(url.toUri(), null, null)
        zLog("$tag: mayLaunchUrl: $isLaunched")

        runCatching {
            if (customTabsSession != null && customTabsSession.isEngagementSignalsApiAvailable(Bundle.EMPTY)) {
                val engagementSignalsCallback = object : EngagementSignalsCallback {
                    private val tag = EngagementSignalsCallback::class.simpleName

                    override fun onVerticalScrollEvent(isDirectionUp: Boolean, extras: Bundle) {
                        zLog("$tag: onVerticalScrollEvent: ${if (isDirectionUp) "UP️" else "DOWN"}, $extras")
                    }

                    override fun onGreatestScrollPercentageIncreased(
                        scrollPercentage: Int, extras: Bundle
                    ) {
                        zLog("$tag: onGreatestScrollPercentageIncreased: $scrollPercentage%")
                    }

                    override fun onSessionEnded(didUserInteract: Boolean, extras: Bundle) {
                        zLog("$tag: onSessionEnded: ${if (didUserInteract) "session ended with user interaction" else "session ended without user interaction"}, $extras")
                    }
                }
                customTabsSession.setEngagementSignalsCallback(engagementSignalsCallback, Bundle.EMPTY)
            }
        }.onFailure(Throwable::printStackTrace)

        val light = CustomTabColorSchemeParams.Builder()
            // .setToolbarColor(Color.RED)
            .build()
        val dark = CustomTabColorSchemeParams.Builder()
            // .setToolbarColor(Color.GREEN)
            .build()
        val customTabsIntent = CustomTabsIntent.Builder(customTabsSession)
            .setDefaultColorSchemeParams(light)
            .setColorSchemeParams(CustomTabsIntent.COLOR_SCHEME_DARK, dark)
            // .setStartAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out)
            // .setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            .setUrlBarHidingEnabled(true)
            .setShowTitle(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_OFF)
            // .setCloseButtonIcon(Bitmap)
            // .setSecondaryToolbarViews(...)
            .setBookmarksButtonEnabled(false)
            .setDownloadButtonEnabled(false)
            .setToolbarCornerRadiusDp(4)
            .setBackgroundInteractionEnabled(false)
            .setOpenInBrowserButtonState(CustomTabsIntent.OPEN_IN_BROWSER_STATE_OFF) // exception thrown
            .setShareIdentityEnabled(true)
            .setSendToExternalDefaultHandlerEnabled(true)
            .setTranslateLocale(Locale.JAPAN)
            .setEphemeralBrowsingEnabled(CustomTabsClient.isEphemeralBrowsingSupported(context, packageNameCt))
            .build()
        val intent = customTabsIntent.intent
        intent.putExtra(Intent.EXTRA_REFERRER, "android-app://${context.packageName}".toUri())

        runCatching {
            customTabsIntent.launchUrl(context, url.toUri())
        }.onFailure {
            it.printStackTrace()
            zLog("$tag: $packageNameCt does not support CT.")
        }
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        zLog("$tag: onServiceDisconnected: $name")
        customTabsClient = null
        customTabsSession = null
    }

    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
        zLog("$tag: onBindingDied: $name")
    }

    override fun onNullBinding(name: ComponentName?) {
        super.onNullBinding(name)
        zLog("$tag: onNullBinding: $name")
    }

    fun destroy() {
        customTabsClient = null
        customTabsSession = null
    }
}