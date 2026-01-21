package net.imknown.testandroid.ext

import android.util.Log

fun Any.zLog(msg: Any?) {
    val tag = when {
        javaClass.simpleName.isNotEmpty() -> javaClass.simpleName
        javaClass.interfaces.isNotEmpty() -> javaClass.interfaces[0].simpleName
        javaClass.superclass != null -> javaClass.superclass?.simpleName
        else -> "Anonymous"
    }
    Log.e("zzz $tag", msg.toString())
}