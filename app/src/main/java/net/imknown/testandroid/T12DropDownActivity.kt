package net.imknown.testandroid

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ListPopupWindow
import androidx.appcompat.widget.PopupMenu
import net.imknown.testandroid.databinding.T12ActivityDropDownBinding

/** https://material.io/components/menus/android */
class T12DropDownActivity : AppCompatActivity() {
    private val binding by lazy { T12ActivityDropDownBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        preparePopupMenu()

        prepareListPopupWindow()
    }

    private fun preparePopupMenu() {
        binding.menuButton.setOnClickListener {
            with(PopupMenu(this, it)) {
                menuInflater.inflate(R.menu.t12_popup_menu, menu)
                show()
            }
        }
    }

    private fun prepareListPopupWindow() {
        val listPopupWindow = ListPopupWindow(this, null, android.R.attr.listPopupWindowStyle)

        listPopupWindow.anchorView = binding.listPopupButton

        val items = listOf("Option 1", "Option 2", "Option 3")
        val adapter = ArrayAdapter(this, R.layout.t12_list_popup_window_item, items)
        listPopupWindow.setAdapter(adapter)

        listPopupWindow.setOnItemClickListener { _: AdapterView<*>?, _: View?, _: Int, _: Long ->
            // Respond to list popup window item click.

            // Dismiss popup.
            listPopupWindow.dismiss()
        }

        binding.listPopupButton.setOnClickListener { listPopupWindow.show() }
    }
}