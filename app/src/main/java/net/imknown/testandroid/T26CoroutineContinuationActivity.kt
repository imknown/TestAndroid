package net.imknown.testandroid

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import net.imknown.testandroid.ext.zLog
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.seconds

class T26CoroutineContinuationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val contentView = findViewById<View>(android.R.id.content)

        suspend fun getX() = withTimeoutOrNull(5.seconds) {
            suspendCancellableCoroutine { continuation ->
                contentView.setOnClickListener {
                    if (continuation.isActive) {
                        continuation.resume(true)
                    }
                }

                continuation.invokeOnCancellation {
                    contentView.setOnClickListener(null)
                }
            }
        }

        val flowX = callbackFlow {
            contentView.setOnClickListener {
                trySend(true)
            }

            awaitClose {
                contentView.setOnClickListener(null)
            }
        }.take(1)
            .timeout(5.seconds)
            .catch { e ->
                if (e is TimeoutCancellationException) {
                    emit(false)
                } else {
                    throw e
                }
            }
            .flowWithLifecycle(lifecycle)

        lifecycleScope.launch {
            val oneshotX = getX()
            zLog("xxx oneshotX: $oneshotX")

            flowX.collect {
                zLog("xxx flowX: $it")
            }
        }

        zLog("xxx after launch")
    }
}