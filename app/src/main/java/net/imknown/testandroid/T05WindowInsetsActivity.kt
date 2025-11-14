package net.imknown.testandroid

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import net.imknown.testandroid.databinding.T05ActivityWindowInsetsBinding

class T05WindowInsetsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        val binding = T05ActivityWindowInsetsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.root.setOnClickListener {
            binding.editText1.clearFocus()
        }
    }
}