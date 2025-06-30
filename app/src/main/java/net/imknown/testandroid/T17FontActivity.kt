package net.imknown.testandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.imknown.testandroid.databinding.T17ActivityFontBinding

class T17FontActivity : AppCompatActivity() {
    private val binding by lazy { T17ActivityFontBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
    }
}