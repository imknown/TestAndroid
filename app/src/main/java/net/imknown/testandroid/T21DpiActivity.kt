package net.imknown.testandroid

import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
import androidx.appcompat.app.AppCompatActivity
import net.imknown.testandroid.databinding.T21ActivityDpiBinding

class T21DpiActivity : AppCompatActivity() {
    private val binding by lazy { T21ActivityDpiBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        with(binding.tvResult) {
            text = getResult()
            setOnClickListener {
                text = getResult()
            }
        }
    }

    private fun getResult(): String {
        var result = "(P) Physical, (L) Logical, (W) Window size, (S) Screen size, (*) Click to change\n\n"

         val display: Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display
        } else {
            @Suppress("DEPRECATION")
            windowManager?.defaultDisplay
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val mode = display?.mode
            val physicalWidth = mode?.physicalWidth
            val physicalHeight = mode?.physicalHeight
            result += "display.mode\n(P, S, 11+) Width: $physicalWidth, Height: $physicalHeight\n\n"
        }

        val xdpi = resources.displayMetrics.xdpi
        val ydpi = resources.displayMetrics.ydpi
        result += "resources.displayMetrics\n(P) xdpi: $xdpi, ydpi: $ydpi\n\n"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val maximumWindowMetrics = windowManager.maximumWindowMetrics
            val bounds = maximumWindowMetrics.bounds
            val widthPixels = bounds.width()
            val heightPixels = bounds.height()
            result += "maximumWindowMetrics\n(L, S, 11+) Width: $widthPixels, Height: $heightPixels\n\n"
        }

        val decorView = window.decorView

        val windowVisibleDisplayFrame = Rect()
        decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame)
        result += "decorView.getWindowVisibleDisplayFrame\n(L, W, *) ${windowVisibleDisplayFrame.toShortString()}\n\n" // Approximately??

        val locationOnScreen = IntArray(2)
        decorView.getLocationOnScreen(locationOnScreen)
        val pointOnScreen = Point(locationOnScreen[0], locationOnScreen[1])
        result += "decorView.getLocationOnScreen\n(L, W) $pointOnScreen\n\n"

        val locationInWindow = IntArray(2)
        decorView.getLocationInWindow(locationInWindow)
        val pointInWindow = Point(locationInWindow[0], locationInWindow[1])
        result += "decorView.getLocationInWindow\n(L, W) $pointInWindow\n\n" // Should always be (0, 0)?

        val realSizePoint = Point()
        @Suppress("DEPRECATION")
        display?.getRealSize(realSizePoint)
        val realWidth = realSizePoint.x
        val realHeight = realSizePoint.y
        result += "display.getRealSize\n(L, S) Width: $realWidth, Height: $realHeight\n\n"

        val sizePoint = Point()
        @Suppress("DEPRECATION")
        display?.getSize(sizePoint)
        val width = sizePoint.x
        val height = sizePoint.y
        result += "display.getSize\n(L, W) Width: $width, Height: $height\n\n"

        val globalVisibleRect = Rect()
        decorView.getGlobalVisibleRect(globalVisibleRect)
        result += "decorView.getGlobalVisibleRect\n(L, W, *) ${globalVisibleRect.toShortString()}\n\n"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val currentWindowMetrics = windowManager.currentWindowMetrics
            val bounds = currentWindowMetrics.bounds
            val widthPixels = bounds.width()
            val heightPixels = bounds.height()
            result += "currentWindowMetrics\n(L, W, 11+) Width: $widthPixels, Height: $heightPixels\n\n"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val density = currentWindowMetrics.density
                val dpi = density * DisplayMetrics.DENSITY_DEFAULT
                result += "currentWindowMetrics\n(14+) density: $density, (L) DPI: $dpi\n\n"
            }
        }

        val resourcesDisplayMetricsWidthPixels = resources.displayMetrics.widthPixels
        val resourcesDisplayMetricsHeightPixels = resources.displayMetrics.heightPixels
        val resourcesDisplayMetricsDensityDpi = resources.displayMetrics.densityDpi
        val resourcesDisplayMetricsXdpi = resources.displayMetrics.xdpi
        val resourcesDisplayMetricsYdpi = resources.displayMetrics.ydpi
        result += "resources.displayMetrics\n(L, W) Width: $resourcesDisplayMetricsWidthPixels, Height: $resourcesDisplayMetricsHeightPixels, (L) densityDpi: $resourcesDisplayMetricsDensityDpi, (P) xdpi: $resourcesDisplayMetricsXdpi, ydpi: $resourcesDisplayMetricsYdpi\n\n"

        val systemResources = Resources.getSystem()
        val systemResourcesWidthPixels = systemResources.displayMetrics.widthPixels
        val systemResourcesHeightPixels = systemResources.displayMetrics.heightPixels
        val systemResourcesDensityDpi = systemResources.displayMetrics.densityDpi
        val systemResourcesXdpi = systemResources.displayMetrics.xdpi
        val systemResourcesYdpi = systemResources.displayMetrics.ydpi
        result += "Resources.getSystem().displayMetrics\n(L, W) Width: $systemResourcesWidthPixels, Height: $systemResourcesHeightPixels, (L) densityDpi: $systemResourcesDensityDpi, (P) xdpi: $systemResourcesXdpi, ydpi: $systemResourcesYdpi\n\n"

        val activityConfiguration = resources.configuration
        val activityConfigurationScreenWidthDp = activityConfiguration.screenWidthDp
        val activityConfigurationScreenHeightDp = activityConfiguration.screenHeightDp
        val activityConfigurationDensityDpi = activityConfiguration.densityDpi
        result += "activityConfiguration.displayMetrics\n(L, W) screenWidthDp: $activityConfigurationScreenWidthDp, screenHeightDp: $activityConfigurationScreenHeightDp, (L) densityDpi: $activityConfigurationDensityDpi\n\n"

        val systemConfiguration = systemResources.configuration
        val systemConfigurationScreenWidthDp = systemConfiguration.screenWidthDp
        val systemConfigurationScreenHeightDp = systemConfiguration.screenHeightDp
        val systemConfigurationDensityDpi = systemConfiguration.densityDpi
        result += "systemConfiguration.displayMetrics\n(L, W) screenWidthDp: $systemConfigurationScreenWidthDp, screenHeightDp: $systemConfigurationScreenHeightDp, (L) densityDpi: $systemConfigurationDensityDpi\n\n"

        return result
    }
}