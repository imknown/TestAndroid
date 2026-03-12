package net.imknown.testandroid

import android.app.ComponentCaller
import android.app.PictureInPictureUiState
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.LocaleListCompat
import net.imknown.testandroid.databinding.T01ActivityLifecycleBinding
import net.imknown.testandroid.ext.zLog

open class T01LifecycleActivity : AppCompatActivity() {
    private val binding by lazy { T01ActivityLifecycleBinding.inflate(layoutInflater) }

    override fun onWindowAttributesChanged(params: WindowManager.LayoutParams?) {
        super.onWindowAttributesChanged(params)
        // zLog("onWindowAttributesChanged(params): params = $params")
    }

    override fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean) {
        super.onApplyThemeResource(theme, resid, first)
        zLog("onApplyThemeResource(theme, resid, first): theme = $theme, resid = $resid, first = $first")
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs).also {
            // zLog("onCreateView(name, context, attrs): name = $name, context = $context, attrs = $attrs, view = $it")
        }
    }

    override fun onCreateView(
        parent: View?, name: String, context: Context, attrs: AttributeSet
    ): View? {
        return super.onCreateView(parent, name, context, attrs).also {
            // zLog("onCreateView(parent, name, context, attrs): parent = $parent, name = $name, context = $context, attrs = $attrs, view = $it")
        }
    }

    override fun onContentChanged() {
        super.onContentChanged()
        zLog("onContentChanged()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        zLog("onCreate(savedInstanceState): @${Integer.toHexString(hashCode())} savedInstanceState = $savedInstanceState, intent = $intent, resources.configuration = ${resources.configuration}")

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
        zLog("onCreate(savedInstanceState, persistentState): savedInstanceState = $savedInstanceState, persistentState = $persistentState")
    }

    override fun onStart() {
        super.onStart()
        zLog("onStart()")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        zLog("onRestoreInstanceState(savedInstanceState): $savedInstanceState")
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?, persistentState: PersistableBundle?,
    ) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        zLog("onRestoreInstanceState(savedInstanceState, persistentState): $savedInstanceState, $persistentState")
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        zLog("onPostCreate(savedInstanceState): savedInstanceState = $savedInstanceState")
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        zLog("onPostCreate(savedInstanceState, persistentState): savedInstanceState = $savedInstanceState, persistentState = $persistentState")
    }

    override fun onStateNotSaved() {
        super.onStateNotSaved()
        zLog("onStateNotSaved()")
    }

    override fun onResume() {
        super.onResume()
        zLog("onResume()")
    }

    override fun onPostResume() {
        super.onPostResume()
        zLog("onPostResume()")
    }

    override fun onTopResumedActivityChanged(isTopResumedActivity: Boolean) {
        super.onTopResumedActivityChanged(isTopResumedActivity)
        zLog("onTopResumedActivityChanged(isTopResumedActivity): isTopResumedActivity = $isTopResumedActivity")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        zLog("onAttachedToWindow()")
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        zLog("onWindowFocusChanged(): hasFocus = $hasFocus")
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        zLog("onUserInteraction()")
    }

    override fun onProvideReferrer(): Uri? {
        return super.onProvideReferrer().also {
            zLog("onProvideReferrer(): $it")
        }
    }

    override fun onPictureInPictureRequested(): Boolean {
        return super.onPictureInPictureRequested().also {
            zLog("onPictureInPictureRequested(): result = $it")
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        zLog("onUserLeaveHint()")
    }

    override fun onPause() {
        super.onPause()
        zLog("onPause()")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        zLog("onTrimMemory(level): level = $level")
    }

    override fun onCreateDescription(): CharSequence? {
        return super.onCreateDescription().also {
            zLog("onCreateDescription(): result = $it")
        }
    }

    override fun onStop() {
        super.onStop()
        zLog("onStop()")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        zLog("onSaveInstanceState(outState): outState = $outState")
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        zLog("onSaveInstanceState(outState, outPersistentState): outState = $outState, outPersistentState = $outPersistentState")
    }

    /** Back to [onStart] */
    override fun onRestart() {
        super.onRestart()

        zLog("onRestart()")
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()
        zLog("onEnterAnimationComplete()")
    }

    // region [Runtime result]
    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        zLog("onActivityReenter(resultCode, data): resultCode = $resultCode, data = $data")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        zLog("onActivityResult(requestCode, resultCode, data): requestCode = $requestCode, resultCode = $resultCode, data = $data")
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?, caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
        zLog("onActivityResult(requestCode, resultCode, data): requestCode = $requestCode, resultCode = $resultCode, data = $data, caller = $caller")
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        zLog("onNewIntent(intent), old intent: ${this.intent}, new intent: $intent")
        this.intent = intent
    }

    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        zLog("onNewIntent(intent, caller), old intent: ${this.intent}, new intent: $intent, caller: $caller")
        this.intent = intent
    }

    override fun onLocalesChanged(locales: LocaleListCompat) {
        super.onLocalesChanged(locales)
        zLog("onLocalesChanged(locales): locales = $locales")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        zLog("onConfigurationChanged(): newConfig = $newConfig")
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)
        zLog("onMultiWindowModeChanged(isInMultiWindowMode, newConfig): isInMultiWindowMode = $isInMultiWindowMode, newConfig = $newConfig")
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        zLog("onNightModeChanged(mode): mode = $mode")
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean, newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        zLog("onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig): isInPictureInPictureMode = $isInPictureInPictureMode, newConfig = $newConfig")
    }

    override fun onPictureInPictureUiStateChanged(pipState: PictureInPictureUiState) {
        super.onPictureInPictureUiStateChanged(pipState)
        zLog("onPictureInPictureUiStateChanged(pipState): pipState = $pipState")
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {
        super.onPointerCaptureChanged(hasCapture)
        zLog("onPointerCaptureChanged(hasCapture): hasCapture = $hasCapture")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        zLog("onRequestPermissionsResult(requestCode, permissions, grantResults): requestCode = $requestCode, permissions = $permissions, grantResults = $grantResults")
    }
    // endregion [Runtime result]

    override fun onLowMemory() {
        super.onLowMemory()
        zLog("onLowMemory()")
    }

    override fun onDestroy() {
        super.onDestroy()
        zLog("onDestroy()")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        zLog("onDetachedFromWindow()")
    }
}