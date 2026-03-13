package net.imknown.testandroid

import android.animation.Animator
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Button
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import net.imknown.testandroid.ext.zLog

open class LifecycleFragment : Fragment() {

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return super.onCreateAnimation(transit, enter, nextAnim).also {
            zLog("onCreateAnimation(), transit = $transit, enter = $enter, nextAnim = $nextAnim, animation = $it")
        }
    }

    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
        return super.onCreateAnimator(transit, enter, nextAnim).also {
            zLog("onCreateAnimator(), transit = $transit, enter = $enter, nextAnim = $nextAnim, animator = $it")
        }
    }

    override fun onInflate(context: Context, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)
        zLog("onInflate(), context = $context, attrs = $attrs, savedInstanceState = $savedInstanceState")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        zLog("onAttach()")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        zLog("onCreate(), savedInstanceState = $savedInstanceState")
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        return super.onGetLayoutInflater(savedInstanceState).also {
            zLog("onGetLayoutInflater(savedInstanceState): savedInstanceState = $savedInstanceState, inflater = $it")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return Button(context).apply { isGone = true }.also {
            zLog("onCreateView(), inflater = $inflater, container = $container, savedInstanceState = $savedInstanceState, view = $it")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        zLog("onViewCreated()")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        zLog("onViewStateRestored(savedInstanceState): savedInstanceState = $savedInstanceState")
    }

    override fun onStart() {
        super.onStart()
        zLog("onStart()")
    }

    override fun onResume() {
        super.onResume()
        zLog("onResume()")
    }

    override fun onPause() {
        super.onPause()
        zLog("onPause()")
    }

    override fun onStop() {
        super.onStop()
        zLog("onStop()")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        zLog("onSaveInstanceState(outState): outState = $outState")
    }

    // region [Runtime result]
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        zLog("onHiddenChanged(), hidden = $hidden")
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        super.onMultiWindowModeChanged(isInMultiWindowMode)
        zLog("onMultiWindowModeChanged(), isInMultiWindowMode = $isInMultiWindowMode")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        zLog("onConfigurationChanged(), newConfig = $newConfig")
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        zLog("onPictureInPictureModeChanged(), isInPictureInPictureMode = $isInPictureInPictureMode")
    }
    // endregion [Runtime result]

    override fun onLowMemory() {
        super.onLowMemory()
        zLog("onLowMemory()")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        zLog("onDestroyView()")
    }

    override fun onDestroy() {
        super.onDestroy()
        zLog("onDestroy()")
    }

    override fun onDetach() {
        super.onDetach()
        zLog("onDetach()")
    }
}