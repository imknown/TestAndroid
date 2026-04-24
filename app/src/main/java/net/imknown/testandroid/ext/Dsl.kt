package net.imknown.testandroid.ext

val android get() = Android()
fun android(block: Android.() -> Unit) = android.apply(block)

class Android {
    var namespace: String = ""

    val dependencies get() = DependencyBuilder()
    fun dependencies(block: DependencyBuilder.() -> Unit) = dependencies.block()
}

class DependencyBuilder {
    fun implementation(lib: String) = println("Adding dependency: $lib")
}

fun main() {
    val namespace =  "com.example.test"
    val lib = "$namespace:test"

    android {
        this.namespace = namespace
        dependencies {
            implementation(lib)
        }
    }

    android.namespace = namespace
    android.dependencies.implementation(lib)
}

