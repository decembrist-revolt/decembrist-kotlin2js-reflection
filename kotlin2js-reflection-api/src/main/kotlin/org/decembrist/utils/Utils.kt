package org.decembrist.utils

import org.decembrist.model.FunctionIdentifier
import org.decembrist.reflection.Reflection
import org.decembrist.services.ReflectionService
import org.decembrist.services.ReflectionService.isCacheEnabled
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

val <T : Any> KClass<T>.jsReflect
    get() = ReflectionService.jsReflectOf(this)

val KFunction<*>.jsReflect
    get() = ReflectionService.jsReflectOf(this)

/**
 * Enable/Disable jsReflect cache
 */
var Reflection.enableCache
    get() = isCacheEnabled
    set(enable) {
        isCacheEnabled = enable
    }

/**
 * Clear jsReflect cache
 */
fun Reflection.clearClassCache() = ReflectionService.clearClassCache()

fun getIdentifier(kFunction: KFunction<*>) = FunctionIdentifier(
        kFunction.name,
        kFunction.toString(),
        kFunction
)

fun <T: KFunction<*>> getIdentifierBySupplier(kFunction: T): FunctionIdentifier {
    return FunctionIdentifier(
            kFunction.name,
            kFunction.toString(),
            kFunction
    )
}

fun FunctionIdentifier.isMethod() = body.matches("^function.*\\(\\\$receiver")

internal external object Object {
    fun create(prototype: dynamic): dynamic
}

internal fun Object.create() = Object.create(null)