package net.imknown.testandroid

import android.graphics.Paint.FontMetricsInt
import android.os.Build
import android.os.Bundle
import android.text.Spanned
import android.text.style.LineHeightSpan
import android.util.TypedValue
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.toSpannable
import net.imknown.testandroid.databinding.T06ActivityTextLineHeightBinding
import kotlin.math.roundToInt

/**
 * - https://developer.android.com/about/versions/13/features#line-height
 * - https://developer.android.com/about/versions/15/behavior-changes-15#elegant-text-height
 */
class T06TextViewLineHeightActivity : AppCompatActivity() {

    private val binding by lazy { T06ActivityTextLineHeightBinding.inflate(layoutInflater) }

    private val isAtLeastAndroid9 = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        initView()
    }

    private val myLineHeightInPx by lazy { 70F.sp }

    private var defaultLineHeightInPx = 0

    private val myLineHeightSpan: LineHeightSpan by lazy {
        if (isAtLeastAndroid9) {
            // noinspection "NewApi"
            LineHeightSpan.Standard(myLineHeightInPx)
        } else {
            LineHeightSpanStandardCompat(myLineHeightInPx)
        }
    }

    private fun initView() {
        // region [CheckBoxes]
        binding.cbIncludeFontPadding.isChecked = binding.tv.includeFontPadding
        binding.cbIncludeFontPadding.setOnCheckedChangeListener { _, isChecked ->
            binding.tv.includeFontPadding = isChecked
        }

        binding.cbFallbackLineHeight.isChecked = if (isAtLeastAndroid9) {
            // noinspection "NewApi"
            binding.tv.isFallbackLineSpacing
        } else {
            // Android 9+ default: fallbackLineSpacing = true
            false
        }
        binding.cbFallbackLineHeight.isEnabled = isAtLeastAndroid9
        binding.cbFallbackLineHeight.setOnCheckedChangeListener { _, isChecked ->
            // noinspection "NewApi"
            binding.tv.isFallbackLineSpacing = isChecked
        }

        // private external fun nGetElegantTextHeight(paintPtr: Long): Int
        binding.cbElegantLineHeight.isChecked = with(binding.tv) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                isElegantTextHeight
            } else {
                @Suppress("DEPRECATION")
                paint.isElegantTextHeight
            }
        }
        binding.cbElegantLineHeight.setOnCheckedChangeListener { _, isChecked ->
            binding.tv.isElegantTextHeight = isChecked
        }

        defaultLineHeightInPx = binding.tv.lineHeight
        binding.cbLineHeightSpan.isChecked = false
        binding.cbLineHeightSpan.setOnCheckedChangeListener { _, isChecked ->
            val spannable = binding.tv.text.toSpannable()
             if (isChecked) {
                spannable.setSpan(myLineHeightSpan, 0, spannable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                spannable.removeSpan(myLineHeightSpan)
            }
            binding.tv.setText(spannable)
        }
        // endregion [CheckBoxes]
    }

    private val Float.sp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, this, resources.displayMetrics
        ).toInt()

    /** Duplicated from [LineHeightSpan.Standard] */
    private class LineHeightSpanStandardCompat(
        @param:Px @param:IntRange(from = 1) val height: Int
    ) : LineHeightSpan {
        override fun chooseHeight(
            text: CharSequence, start: Int, end: Int,
            spanstartv: Int, lineHeight: Int,
            fm: FontMetricsInt
        ) {
            val originHeight = fm.descent - fm.ascent
            val ratio = height * 1.0f / originHeight
            fm.descent = (fm.descent * ratio).roundToInt()
            fm.ascent = fm.descent - height
        }
    }

}