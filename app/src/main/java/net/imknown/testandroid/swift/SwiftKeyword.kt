package net.imknown.testandroid.swift

/** Keyword `guard`. */
fun guard(condition: () -> Boolean): Boolean = condition()

/**
 * Keyword `else` for keyword `guard`.
 *
 * `guard condition else { /* to-do */ }` in Swift, means
 *
 * `if (condition.not()) { /* to-do */ }` in Kotlin.
 *
 * Sample:
 * ```
 * // swift
 * let a: Int? = 0
 * guard let a, a > 0 else { return }
 * ```
 *
 * ```
 * // kotlin
 * val a: Int? = 0
 * guard { a != null && a > 0 } `else` { return }
 * ```
 */
inline infix fun Boolean.`else`(block: () -> Unit) {
    if (not()) {
        block()
    }
}

inline fun guard(condition: Boolean, `else`: () -> Unit) {
    if (!condition) {
        `else`()
    }
}

inline fun guard(condition: () -> Boolean, `else`: () -> Unit) {
    if (!condition()) {
        `else`()
    }
}