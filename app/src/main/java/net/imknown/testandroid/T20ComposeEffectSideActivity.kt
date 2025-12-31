package net.imknown.testandroid

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class T20ComposeEffectSideActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

@Composable
fun App() {

}

@Suppress("unused")
@Composable
fun Placeholder() {
    JustLaunchEffect()
    JustSideEffect()
    JustDisposableEffect()
    JustProduceState()
    JustRememberCoroutineScope()
    JustDerivedStateOf()
    JustSnapshotFlow()
    JustRememberUpdatedState(0)
    StrongSkippingMode()
}

@Composable
fun JustLaunchEffect() {
    var timer by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Time $timer")
    }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(1000)
            timer++
        }
    }
}

@Composable
fun JustSideEffect() {
    var timer by remember { mutableIntStateOf(0) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Time $timer")
    }

    SideEffect {
        Thread.sleep(1000)
        timer++
    }
}

@Composable
fun JustDisposableEffect() {
    var timerStartStop by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                timerStartStop = !timerStartStop
            }) {
                Text(if (timerStartStop) "Stop" else "Start")
            }
        }
    }

    val context = LocalContext.current

    DisposableEffect(key1 = timerStartStop) {
        val x = (1..10).random()
        Toast.makeText(context, "Start $x", Toast.LENGTH_SHORT).show()

        onDispose {
            Toast.makeText(context, "Stop $x", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun JustProduceState() {
    var timerStartStop by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val timer by produceState(initialValue = 0, timerStartStop) {
        val x = (1..10).random()
        var job: Job? = null
        Toast.makeText(context, "Start $x", Toast.LENGTH_SHORT).show()
        if (timerStartStop) {
            // Use MainScope to ensure awaitDispose is triggered
            job = MainScope().launch {
                while (true) {
                    delay(1000)
                    value++
                }
            }
        }

        awaitDispose {
            Toast.makeText(context, "Done $x", Toast.LENGTH_SHORT).show()
            job?.cancel()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Time $timer")
            Button(onClick = {
                timerStartStop = !timerStartStop
            }) {
                Text(if (timerStartStop) "Stop" else "Start")
            }
        }
    }
}

@Composable
fun JustRememberCoroutineScope() {
    val scope = rememberCoroutineScope()
    var timer by remember { mutableIntStateOf(0) }
    var timerStartStop by remember { mutableStateOf(false) }
    var job: Job? by remember { mutableStateOf(null) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Time $timer")
            Button(onClick = {
                job?.cancel()

                timerStartStop = !timerStartStop

                if (timerStartStop) {
                    job = scope.launch {
                        while (true) {
                            delay(1000)
                            timer++
                        }
                    }
                }
            }) {
                Text(if (timerStartStop) "Stop" else "Start")
            }
        }
    }
}

@Composable
fun JustDerivedStateOf() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            items(1000) { index ->
                Text(text = "Item: $index")
            }
        }

        val showButtonDerive by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0
            }
        }

        Log.d("Track", "Recompose")
        Column {
            AnimatedVisibility(showButtonDerive) {
                Button({}) {
                    Text("Row 1 hiding")
                }
            }
        }
    }
}

@Composable
fun JustSnapshotFlow() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            items(1000) { index ->
                Text(text = "Item: $index")
            }
        }

        var showButtonSnapshot by remember {
            mutableStateOf(false)
        }

        Log.d("Track", "Recompose")
        Column {
            AnimatedVisibility(showButtonSnapshot) {
                Button({}) {
                    Text("Row 1 and 2 hiding")
                }
            }
        }

        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex }
                .map { index -> index > 2 }
                .distinctUntilChanged()
                .collect {
                    showButtonSnapshot = it
                }
        }
    }
}

@Composable
fun JustRememberUpdatedState(
    timer: Int
) {
    Text("Time $timer")
    val myRememberTimer by rememberUpdatedState(timer)
    LaunchedEffect(key1 = Unit) {
        delay(1000)
        Log.d("Track", "$myRememberTimer")
    }
}

@Composable
fun StrongSkippingMode() {
    // Approach 1
    val items = remember { mutableStateListOf<Int>() }
    val size = items.size
    Button({ items += (size + 1) }) { Text("添加") }
    LazyColumn {
        items(items, key = { it }) {
            Text(text = it.toString())
        }
    }

    // Approach 2
    var items2 by remember { mutableStateOf(emptyList<Int>()) }
    val size2 = items2.size
    Button({ items2 += (size2 + 1) }) { Text("添加 2") }
    LazyColumn {
        items(items2, key = { it }) {
            Text(text = it.toString())
        }
    }
}
}