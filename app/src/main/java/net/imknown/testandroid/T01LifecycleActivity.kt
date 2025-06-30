package net.imknown.testandroid

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import net.imknown.testandroid.databinding.T01ActivityLifecycleBinding
import net.imknown.testandroid.ext.zLog

open class T01LifecycleActivity : AppCompatActivity() {
    private val binding by lazy { T01ActivityLifecycleBinding.inflate(layoutInflater) }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        zLog("onRestoreInstanceState(1)")
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?, persistentState: PersistableBundle?,
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)

        zLog("onRestoreInstanceState(2)")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        zLog(
            "onCreate(${if (savedInstanceState == null) "null" else "bundle"})" +
                    " @${Integer.toHexString(hashCode())}, $intent"
        )

        zLog("onCreate(), resources.configuration: ${resources.configuration}")

        init()
    }

    private fun init() {
        binding.btnLandscape.setOnClickListener {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        binding.btnPortrait.setOnClickListener {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        zLog("onCreate(2)")
    }

    override fun onStart() {
        super.onStart()

        zLog("onStart()")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        zLog("onNewIntent, old: ${this.intent}, new: $intent")

        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()

        zLog("onResume()")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        zLog("onConfigurationChanged: $newConfig")
    }

    override fun onPause() {
        super.onPause()

        zLog("onPause()")
    }

    override fun onStop() {
        super.onStop()

        zLog("onStop()")
    }

    /** Back to [onStart] */
    override fun onRestart() {
        super.onRestart()

        zLog("onRestart()")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        zLog("onSaveInstanceState(1)")
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        zLog("onSaveInstanceState(2)")
    }

    override fun onDestroy() {
        super.onDestroy()

        zLog("onDestroy()")
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        zLog("onUserLeaveHint")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        zLog("onWindowFocusChanged hasFocus = $hasFocus")
    }
}