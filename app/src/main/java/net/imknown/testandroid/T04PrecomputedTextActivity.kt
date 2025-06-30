package net.imknown.testandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import net.imknown.testandroid.databinding.T04ActivityPrecomputedTextBinding

class T04PrecomputedTextActivity : AppCompatActivity() {
    private val binding by lazy { T04ActivityPrecomputedTextBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        usePrecomputedTextCompat()
    }

    private fun usePrecomputedTextCompat() {
        val longContent = "PrecomputedTextCompat"
        val textFuture = PrecomputedTextCompat.getTextFuture(
            longContent, TextViewCompat.getTextMetricsParams(binding.tv), null
        )

        binding.tv.text = textFuture.get()
    }
}