package net.imknown.testandroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.imknown.testandroid.ext.zLog

/**
 * - https://d.android.com/reference/android/content/Intent
 *     - ACTION_PACKAGE_ADDED
 *     - ACTION_PACKAGE_FULLY_REMOVED (Exempted)
 *     - ACTION_PACKAGE_REMOVED
 *     - ACTION_PACKAGE_REPLACED
 *
 * - https://d.android.com/about/versions/oreo/background.html#broadcasts
 * - https://d.android.com/guide/components/broadcast-exceptions
 * - https://d.android.com/training/basics/intents/package-visibility
 */
class T08PackageBroadcastActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initPackageReceivers()
    }

    private fun initPackageReceivers() {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        registerReceiver(broadcastReceiver, intentFilter)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            zLog(intent.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(broadcastReceiver)
    }
}
