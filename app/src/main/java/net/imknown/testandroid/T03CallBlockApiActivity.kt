package net.imknown.testandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.imknown.testandroid.ext.zLog
import org.chickenhook.restrictionbypass.RestrictionBypass

class T03CallBlockApiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callBlock()
    }

    private fun callBlock() {
        val clazz = Class.forName("dalvik.system.VMRuntime")

        val is64BitAbiMethod = try {
            clazz.getDeclaredMethod("is64BitAbi")
        } catch (e: Exception) {
            zLog(e.message)
            RestrictionBypass.getDeclaredMethod(clazz, "is64BitAbi", String::class.java)
        }

        val is64BitAbi = is64BitAbiMethod.invoke(null, "x86_64") as Boolean
        zLog(is64BitAbi.toString())
    }
}