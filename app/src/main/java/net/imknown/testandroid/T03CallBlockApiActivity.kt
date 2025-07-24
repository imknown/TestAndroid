package net.imknown.testandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class T03CallBlockApiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callBlock()
    }

    private fun callBlock() {
//        val is64BitAbiMethod = RestrictionBypass.getDeclaredMethod(
//            Class.forName("dalvik.system.VMRuntime"),
//            "is64BitAbi", String::class.java
//        )
//
//        val is64BitAbi = is64BitAbiMethod.invoke(null, "x86_64") as Boolean
//        zLog(is64BitAbi.toString())
    }
}