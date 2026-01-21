package net.imknown.testandroid

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsCallback
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

        val customTabsCallback = object: CustomTabsCallback() {
            private val tag = CustomTabsCallback::class.simpleName

            override fun onWarmupCompleted(extras: Bundle) {
                super.onWarmupCompleted(extras)
                zLog("$tag: onWarmupCompleted: $extras")
            }

            override fun onMinimized(extras: Bundle) {
                super.onMinimized(extras)
                zLog("$tag: onMinimized: $extras")
            }

            override fun onUnminimized(extras: Bundle) {
                super.onUnminimized(extras)
                zLog("$tag: onUnminimized: $extras")
            }

            override fun onActivityLayout(
                left: Int, top: Int, right: Int, bottom: Int, state: Int, extras: Bundle
            ) {
                super.onActivityLayout(left, top, right, bottom, state, extras)
                zLog("$tag: onActivityLayout: $left, $top, $right, $bottom, $state, $extras")
            }

            override fun onActivityResized(height: Int, width: Int, extras: Bundle) {
                super.onActivityResized(height, width, extras)
                zLog("$tag: onActivityResized: $height, $width, $extras")
            }

            override fun extraCallback(callbackName: String, args: Bundle?) {
                super.extraCallback(callbackName, args)
                zLog("$tag: extraCallback: $callbackName, $args")
            }

            override fun extraCallbackWithResult(callbackName: String, args: Bundle?): Bundle? {
                zLog("$tag: extraCallbackWithResult: $callbackName, $args")
                return super.extraCallbackWithResult(callbackName, args)
            }

            override fun onMessageChannelReady(extras: Bundle?) {
                super.onMessageChannelReady(extras)
                zLog("$tag: onMessageChannelReady: $extras")
            }

            override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                super.onNavigationEvent(navigationEvent, extras)
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

            override fun onPostMessage(message: String, extras: Bundle?) {
                super.onPostMessage(message, extras)
                zLog("$tag: onPostMessage: $message, $extras")
            }

            override fun onRelationshipValidationResult(
                relation: Int, requestedOrigin: Uri, result: Boolean, extras: Bundle?
            ) {
                super.onRelationshipValidationResult(relation, requestedOrigin, result, extras)
                zLog("$tag: onRelationshipValidationResult: $relation, $requestedOrigin, $result, $extras")

                // val intent: CustomTabsIntent = constructExtraHeadersIntent(customTabsSession)
                // intent.launchUrl(context, url.toUri())
            }

            private fun constructExtraHeadersIntent(session: CustomTabsSession?): CustomTabsIntent {
                val intent = CustomTabsIntent.Builder(session).build()

                // Example non-cors-approvelisted headers.
                val headers = Bundle()
                headers.putString("bearer-token", "Some token")
                headers.putString("redirect-url", "Some redirect url")
                intent.intent.putExtra(Browser.EXTRA_HEADERS, headers)
                return intent
            }
        }
        val customTabsSession = customTabsClient.newSession(customTabsCallback)

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