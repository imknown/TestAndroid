package net.imknown.testandroid

import android.os.Bundle
import androidx.browser.customtabs.CustomTabsSession
import androidx.browser.customtabs.EngagementSignalsCallback
import net.imknown.testandroid.ext.zLog

class T24EngagementSignalsCallback : EngagementSignalsCallback {
    private val tag = T24EngagementSignalsCallback::class.simpleName

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

    fun setup(customTabsSession: CustomTabsSession?) {
        customTabsSession ?: return

        runCatching {
            if (customTabsSession.isEngagementSignalsApiAvailable(Bundle.EMPTY)) {
                customTabsSession.setEngagementSignalsCallback(this, Bundle.EMPTY)
            }
        }.onFailure(Throwable::printStackTrace)
    }
}