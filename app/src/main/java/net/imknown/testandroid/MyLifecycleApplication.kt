package net.imknown.testandroid

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.material.color.DynamicColors
import net.imknown.testandroid.ext.zLog

class MyLifecycleApplication : Application() {
    companion object {
        private const val TAG = "TAG"
    }

    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)

        registerActivityLifecycleCallbacks()

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                zLog("LifecycleEventObserver " + event.name)
            }
        })

        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                zLog("DefaultLifecycleObserver onCreate")
            }

            override fun onStart(owner: LifecycleOwner) {
                zLog("DefaultLifecycleObserver onStart")
            }

            override fun onStop(owner: LifecycleOwner) {
                zLog("DefaultLifecycleObserver onStop")
            }

            override fun onResume(owner: LifecycleOwner) {
                zLog("DefaultLifecycleObserver onResume")
            }

            override fun onDestroy(owner: LifecycleOwner) {
                zLog("DefaultLifecycleObserver onDestroy")
            }

            override fun onPause(owner: LifecycleOwner) {
                zLog("DefaultLifecycleObserver onPause")
            }
        })

        StrictMode.enableDefaults()
    }

    // region [Way 1]
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            zLog("onTrimMemory")
        }
    }
    // endregion [Way 1]

    private var activityReferences = 0

    private var isActivityChangingConfigurations = false

    private fun registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                (activity as? AppCompatActivity)?.enableEdgeToEdge()

                // region [Opt-out Edge-to-Edge]
                val view = activity.findViewById<View>(android.R.id.content)
                ViewCompat.setOnApplyWindowInsetsListener(view) { view, windowInsets ->
                    val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                    view.updatePadding(
                        left = insets.left,
                        right = insets.right,
                        top = insets.top,
                        bottom = insets.bottom
                    )

                    windowInsets
                }
                // endregion [Opt-out Edge-to-Edge]
            }

            override fun onActivityStarted(activity: Activity) {
                if (++activityReferences == 1 && !isActivityChangingConfigurations) {
                    zLog("onActivityStarted()")
                }

//                val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
//                val rootView = contentView.getChildAt(0) as? ViewGroup
//                    ?: return
//                // ViewGroupCompat.installCompatInsetsDispatch(rootView)
//                ViewCompat.setOnApplyWindowInsetsListener(rootView) { _, windowInsets ->
//                    val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
//
//                    rootView.forEach {
//                        if (it is ViewGroup) {
//                            it.updatePadding(
//                                left = insets.left,
//                                right = insets.right,
//                                top = insets.top,
//                                bottom = insets.bottom
//                            )
//                        } else {
//                            it.updateLayoutParams<ViewGroup.MarginLayoutParams> {
//                                updateMarginsRelative(
//                                    start = insets.left,
//                                    end = insets.right,
//                                    top = insets.top,
//                                    bottom = insets.bottom
//                                )
//                            }
//                        }
//                    }
//
//                    windowInsets
//                }
            }

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                isActivityChangingConfigurations = activity.isChangingConfigurations

                if (--activityReferences == 0 && !isActivityChangingConfigurations) {
                    zLog("onActivityStopped()")
                }
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}