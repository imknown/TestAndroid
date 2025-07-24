package net.imknown.testandroid

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import net.imknown.testandroid.databinding.T05ActivityWindowInsetsBinding
import net.imknown.testandroid.ext.zLog

class T05WindowInsetsActivity : AppCompatActivity() {
    private val binding by lazy { T05ActivityWindowInsetsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        initInsets()
    }

    private fun applyInsets(): WindowInsetsCompat {
        val type = WindowInsetsCompat.Type.navigationBars()
        val rootInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(
            binding.root.rootWindowInsets
        )
        val insets = rootInsetsCompat.getInsets(type)
        binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            updateMarginsRelative(insets.left, insets.top, insets.right, insets.bottom)
        }
        return WindowInsetsCompat.Builder().setInsets(type, insets).build()
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insetsCompat ->
            val displayCutoutInsets =
                insetsCompat.getInsets(WindowInsetsCompat.Type.displayCutout())
            val displayCutoutIgnoringVisibilityInsets =
                insetsCompat.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.displayCutout())
            zLog("displayCutoutInsets: $displayCutoutInsets")
            zLog("displayCutoutIgnoringVisibilityInsets: $displayCutoutIgnoringVisibilityInsets")

            val systemBarsInsets = insetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            val systemBarsIgnoringVisibilityInsets =
                insetsCompat.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
            zLog("systemBarsInsets: $systemBarsInsets")
            zLog("systemBarsIgnoringVisibilityInsets: $systemBarsIgnoringVisibilityInsets")

            val captionBarInsets = insetsCompat.getInsets(WindowInsetsCompat.Type.captionBar())
            val captionBarIgnoringVisibilityInsets =
                insetsCompat.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.captionBar())
            zLog("captionBarInsets: $captionBarInsets")
            zLog("captionBarIgnoringVisibilityInsets: $captionBarIgnoringVisibilityInsets")

            val statusBarsInsets = insetsCompat.getInsets(WindowInsetsCompat.Type.statusBars())
            val statusBarsIgnoringVisibilityInsets =
                insetsCompat.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars())
            zLog("statusBarsInsets: $statusBarsInsets")
            zLog("statusBarsIgnoringVisibilityInsets: $statusBarsIgnoringVisibilityInsets")

            val navigationBarsInsets =
                insetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars())
            val navigationBarsIgnoringVisibilityInsets =
                insetsCompat.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars())
            zLog("navigationBarsInsets: $navigationBarsInsets")
            zLog("navigationBarsIgnoringVisibilityInsets: $navigationBarsIgnoringVisibilityInsets")

            val imeInsets = insetsCompat.getInsets(WindowInsetsCompat.Type.ime())
//            val imeIgnoringVisibilityInsetsInsets =
//                insetsCompat.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.ime())
            zLog("imeInsets: $imeInsets")
//            zLog("imeIgnoringVisibilityInsetsInsets: $imeIgnoringVisibilityInsetsInsets")

//            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
//                updateMarginsRelative(
//                    systemBarsInsets.left,
//                    systemBarsInsets.top - 20,
//                    systemBarsInsets.right,
//                    systemBarsInsets.bottom - 20
//                )
//            }

            insetsCompat
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val inputLayoutMarginBottom = binding.etInput.marginBottom
            val windowInsetsAnimationCallback =
                object : WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                    override fun onStart(
                        animation: WindowInsetsAnimation, bounds: WindowInsetsAnimation.Bounds,
                    ): WindowInsetsAnimation.Bounds {
                        return bounds
                    }

                    override fun onProgress(
                        insets: WindowInsets, animations: MutableList<WindowInsetsAnimation>,
                    ): WindowInsets {
                        val imeInsets = insets.getInsets(WindowInsets.Type.ime())
                        binding.etInput.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                            updateMarginsRelative(bottom = inputLayoutMarginBottom + imeInsets.bottom)
                        }

                        return insets
                    }

                    override fun onEnd(animation: WindowInsetsAnimation) {
                        getImeState(binding.btnState)
                    }
                }
            binding.etInput.setWindowInsetsAnimationCallback(windowInsetsAnimationCallback)
        }
    }

    fun showIme(view: View) {
//        view.windowInsetsController?.show(WindowInsets.Type.ime())
//        window.insetsController?.show(WindowInsets.Type.ime())

        WindowCompat.getInsetsController(window, view).show(WindowInsetsCompat.Type.ime())
    }

    fun hideIme(view: View) {
//        view.windowInsetsController?.hide(WindowInsets.Type.ime())
//        window.insetsController?.hide(WindowInsets.Type.ime())

        WindowCompat.getInsetsController(window, view).hide(WindowInsetsCompat.Type.ime())
    }

    @SuppressLint("SetTextI18n")
    @Suppress("UNUSED_PARAMETER")
    fun getImeState(view: View) {
        val rootInsetsCompat = WindowInsetsCompat.toWindowInsetsCompat(
            binding.root.rootWindowInsets
        )

        val isVisible = rootInsetsCompat.isVisible(WindowInsetsCompat.Type.ime())
        val imeInsets = rootInsetsCompat.getInsets(WindowInsetsCompat.Type.ime())

        zLog("getImeState() imeIsVisible: $isVisible")
        zLog("getImeState() imeInsets: $imeInsets")

        binding.btnState.text = "$isVisible\n" +
                "top: ${imeInsets.top}\n" +
                "bottom: ${imeInsets.bottom}\n" +
                "left: ${imeInsets.left}\n" +
                "right: ${imeInsets.right}"
    }
}