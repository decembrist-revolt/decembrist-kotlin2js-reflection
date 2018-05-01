package org.decembrist.utils

import org.decembrist.reflection.JsReflect
import org.decembrist.reflection.Reflection
import kotlin.reflect.KClass

const val REFLECTION_INFO = "reflection-info"

private var _enableCache = true

private val classChache = mutableMapOf<String, JsReflect<*>>()

val <T : Any> KClass<T>.jsReflect
    get() = jsReflectOf(this)

@Suppress("UNCHECKED_CAST")
private fun <T : Any> jsReflectOf(kClass: KClass<T>): JsReflect<T> {
    val jsName = kClass.asDynamic()[REFLECTION_INFO].jsName.unsafeCast<String>()
    val cachedValue = if (_enableCache) {
        classChache[jsName]?.let { it as JsReflect<T> }
    } else null
    return cachedValue ?: object : JsReflect<T> {
        override val jsName: String
            get() = jsName
        override val jsConstructor: dynamic
            get() = kClass.asDynamic()[REFLECTION_INFO].jsConstructor

        override fun createInstance(arguments: Array<Any>): T {
            return kClass.asDynamic()[REFLECTION_INFO]
                    .createInstance(arguments)
                    .unsafeCast<T>()
        }
    }.apply {
        classChache[jsName] = this
    }
}

/**
 * Clear jsReflect cache
 */
fun Reflection.clearCache() = classChache.clear()

/**
 * Enable/Disable jsReflect cache
 */
var Reflection.enableCache
    get() = _enableCache
    set(enable: Boolean) {
        _enableCache = enable
    }