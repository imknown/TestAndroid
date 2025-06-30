package net.imknown.testandroid

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import net.imknown.testandroid.ext.zLog

open class LifecycleFragment : Fragment() {

    override fun onAttach(context: Context) {
        zLog("onAttach()")
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        zLog("onCreate(), savedInstanceState = $savedInstanceState")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        zLog("onCreateView()")
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        zLog("onViewCreated()")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        zLog("onStart()")
        super.onStart()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        zLog("onHiddenChanged(), hidden = $hidden")
        super.onHiddenChanged(hidden)
    }

    override fun onResume() {
        zLog("onResume()")
        super.onResume()
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        zLog("onMultiWindowModeChanged(), isInMultiWindowMode = $isInMultiWindowMode")
        super.onMultiWindowModeChanged(isInMultiWindowMode)
    }

    override fun onPause() {
        zLog("onPause()")
        super.onPause()
    }

    override fun onStop() {
        zLog("onStop()")
        super.onStop()
    }

    override fun onDestroyView() {
        zLog("onDestroyView()")
        super.onDestroyView()
    }

    override fun onDestroy() {
        zLog("onDestroy()")
        super.onDestroy()
    }

    override fun onDetach() {
        zLog("onDetach()")
        super.onDetach()
    }
}