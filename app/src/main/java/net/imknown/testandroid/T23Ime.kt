package net.imknown.testandroid

import android.inputmethodservice.InputMethodService
import android.view.View
import net.imknown.testandroid.databinding.T23KeyboardBinding

class T23Ime : InputMethodService() {

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onCreateInputView(): View {
        val binding = T23KeyboardBinding.inflate(layoutInflater)

        binding.key1.setOnClickListener {
            currentInputConnection?.commitText("1", 1)
        }
        binding.key2.setOnClickListener {
            currentInputConnection?.commitText("2", 1)
        }
        binding.key3.setOnClickListener {
            currentInputConnection?.commitText("3", 1)
        }

        binding.keyDelete.setOnClickListener {
            currentInputConnection?.deleteSurroundingText(1, 0)
        }
        binding.keyEnter.setOnClickListener {
            currentInputConnection?.sendKeyEvent(
                android.view.KeyEvent(
                    android.view.KeyEvent.ACTION_DOWN,
                    android.view.KeyEvent.KEYCODE_ENTER
                )
            )
        }

        return binding.root
    }
}