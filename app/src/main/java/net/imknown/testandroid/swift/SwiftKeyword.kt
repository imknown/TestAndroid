package net.imknown.testandroid.swift

// region [Swift style]
/** Keyword `guard`. */
fun guard(condition: () -> Boolean) = condition()

/**
 * Keyword guardElse for keyword `guard`.
 *
 * `guard condition else { /* to-do */ }` in Swift, means
 *
 * `if (condition.not()) { /* to-do */ }` in Kotlin.
 *
 * Sample:
 * ``` swift
 * // swift
 * let a: Int? = 0
 * guard let a, a > 0 else { return }
 * ```
 *
 * ``` kotlin
 * // kotlin
 * val a: Int? = 0
 * guard { a != null && a > 0 } guardElse { return }
 * ```
 */
inline infix fun Boolean.guardElse(block: () -> Unit) {
    if (not()) {
        block()
    }
}
// endregion [Swift style]

// region [Kotlin style]
inline fun guard(condition: () -> Boolean, guardElse: () -> Unit) {
    if (!condition()) {
        guardElse()
    }
}
// region [Kotlin style]