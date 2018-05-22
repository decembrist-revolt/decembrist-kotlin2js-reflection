package org.decembrist.utils

/**
 * throw [RuntimeException] if key already exists
 */
fun <T, D> MutableMap<T, D>.putAndCheck(key: T, value: D) {
    if (get(key) == null) {
        put(key, value)
    } else {
        throw RuntimeException("Key: $key already exists")
    }
}

/**
 * throw [RuntimeException] if any key already exists
 */
fun <T, D> MutableMap<T, D>.putAndCheck(map: Map<T, D>) {
    for((key, value) in map) {
        putAndCheck(key, value)
    }
}