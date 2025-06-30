package net.imknown.testandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.imknown.testandroid.databinding.T09ActivitySamsungLeadingMarginApanPaddingBugBinding

class T09SamsungLeadingMarginSpanBugActivity : AppCompatActivity() {

    private val binding by lazy {
        T09ActivitySamsungLeadingMarginApanPaddingBugBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

//        binding.tv.text = SpannableString(binding.tv.text).apply {
//            setSpan(
//                LeadingMarginSpan.Standard(200, 0),
//                0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//        }
    }
}