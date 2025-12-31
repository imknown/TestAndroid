package net.imknown.testandroid

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import net.imknown.testandroid.databinding.T18ActivityFocusBinding

class T18FocusActivity : AppCompatActivity() {
    private val binding by lazy { T18ActivityFocusBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        binding.root.setOnClickListener {
            showDialogFragment()
        }

        binding.btn.setOnClickListener {
            binding.tv.requestFocus()
        }

        binding.tv.setOnKeyListener { _, _, _ ->
            Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show()
            true
        }

        binding.tv.requestFocus()

        val a = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                println("zzz 1")
            }
        }
        onBackPressedDispatcher.addCallback(this, a)

        val b = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                println("zzz 2")
            }
        }
        onBackPressedDispatcher.addCallback(this, b)

        b.remove()
    }

    private fun showDialogFragment() {
        val fragment = MyDialogFragment()
        fragment.show(supportFragmentManager, "TAG")
    }

private class MyDialogFragment : DialogFragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = EditText(context).apply { setText("Hello") }
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton("Start", null)
            .setNegativeButton("Cancel", null)
            .create()
    }
}
}