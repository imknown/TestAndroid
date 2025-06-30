package net.imknown.testandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.imknown.testandroid.ext.zLog

class T02EmojiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        printLogs()
    }

    private fun printLogs() {
        fun ByteArray.toHexString() = joinToString(" ") { "%02X".format(it) }

        arrayOf(
            "",
            "\r",
            "1",
            "Â", "˚", "¬", "æ", "π", "¨", "˝", "¥", "©", "´", "ƒ", "˜",
            "√", "，", "ⴰ", "ⵓ", "…", "“", "†", "∂", "∆", "", "◊", "ⵣ",
            "好", "気", "１", "ヰ", "㋼", "ｱ", "㍿", "㍰",
            "क्", "ශ්ර",
            "🐱", "🏳️‍🌈", "👨‍👩‍👧‍👦"
        ).forEach {
            zLog(
                "$it ->\t" +
                        "byte: ${it.toByteArray().toHexString()}, " +
                        "byte size: ${it.toByteArray().size}, " +
                        "string length: ${it.length}"
            )
        }
    }
}