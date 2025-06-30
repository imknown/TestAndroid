package net.imknown.testandroid.ext

import android.util.Log

fun Any.zLog(msg: Any?) = Log.e("zzz ${this::class.simpleName}", msg.toString())