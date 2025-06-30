package net.imknown.testandroid

import android.R.attr.x
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class T16KotlinKeywordContext : AppCompatActivity(0) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val a = object : A {
            override var x = 1
        }
        with(a) {
            with(AExt()) {
                println(x + y + z)
            }
        }
    }
}

interface A {
    var x: Int
}

// context(A)
class AExt {
    // val x = 2
    val y = 3 * x
    val z = x + 1
}