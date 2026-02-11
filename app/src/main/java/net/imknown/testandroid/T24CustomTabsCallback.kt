package net.imknown.testandroid

import android.net.Uri
import android.os.Bundle
import android.provider.Browser
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsSession
import net.imknown.testandroid.ext.zLog

class T24CustomTabsCallback: CustomTabsCallback() {
    private val tag = T24CustomTabsCallback::class.simpleName

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