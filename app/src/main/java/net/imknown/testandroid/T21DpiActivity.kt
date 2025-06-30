package net.imknown.testandroid

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
        var result = ""

         val display: Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display
        } else {
            @Suppress("DEPRECATION")
            windowManager?.defaultDisplay
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val mode = display?.mode
            val widthPixels3 = mode?.physicalWidth
            val heightPixels3 = mode?.physicalHeight
            result += "(Physical, 11+) display.mode\nW: $widthPixels3, H: $heightPixels3\n\n"
        }

        val xdpi = resources.displayMetrics.xdpi
        val ydpi = resources.displayMetrics.ydpi
        result += "(Physical) resources.displayMetrics\nxdpi: $xdpi, ydpi: $ydpi\n\n"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val maximumWindowMetrics = windowManager.maximumWindowMetrics
            val bounds = maximumWindowMetrics.bounds
            val widthPixels2 = bounds.width()
            val heightPixels2 = bounds.height()
            result += "(Logical, 11+) maximumWindowMetrics\nW: $widthPixels2, H: $heightPixels2\n\n"
        }

        val decorView = window.decorView

        val windowVisibleDisplayFrame = Rect()
        decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame)
        result += "(Window) decorView.getWindowVisibleDisplayFrame\n${windowVisibleDisplayFrame.toShortString()}\n\n" // Approximately??

        val locationOnScreen = IntArray(2)
        decorView.getLocationOnScreen(locationOnScreen)
        val pointOnScreen = Point(locationOnScreen[0], locationOnScreen[1])
        result += "(Logical) decorView.getLocationOnScreen\n$pointOnScreen\n\n"

        val locationInWindow = IntArray(2)
        decorView.getLocationInWindow(locationInWindow)
        val pointInWindow = Point(locationInWindow[0], locationInWindow[1])
        result += "(Window) decorView.getLocationInWindow\n${pointInWindow}\n\n" // Should always be (0, 0)?

        val realSizePoint = Point()
        @Suppress("DEPRECATION")
        display?.getRealSize(realSizePoint)
        val realWidth = realSizePoint.x
        val realHeight = realSizePoint.y
        result += "(Logical) display.getRealSize\nW: $realWidth, H: $realHeight\n\n"

        val sizePoint = Point()
        @Suppress("DEPRECATION")
        display?.getSize(sizePoint)
        val width = sizePoint.x
        val height = sizePoint.y
        result += "(Window) display.getSize\nW: $width, H: $height\n\n"

        val globalVisibleRect = Rect()
        decorView.getGlobalVisibleRect(globalVisibleRect)
        result += "(Window) decorView.getGlobalVisibleRect\n${globalVisibleRect.toShortString()}\n\n"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val currentWindowMetrics = windowManager.currentWindowMetrics
            val bounds = currentWindowMetrics.bounds
            val widthPixels = bounds.width()
            val heightPixels = bounds.height()
            result += "(Window, 11+) currentWindowMetrics\nW: $widthPixels, H: $heightPixels"

            result += if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val density = currentWindowMetrics.density * DisplayMetrics.DENSITY_DEFAULT
                ", (Logical, 14+) DPI: $density"
            } else {
                ""
            } + "\n\n"
        }

        val a = resources.displayMetrics.widthPixels
        val b = resources.displayMetrics.heightPixels
        val densityDpi = resources.displayMetrics.densityDpi
        result += "(Window) resources.displayMetrics\nW: $a, H: $b, (Logical) densityDpi: $densityDpi"

        return result
    }
}