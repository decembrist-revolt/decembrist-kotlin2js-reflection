package org.decembrist.utils

import org.decembrist.model.FunctionIdentifier
import org.decembrist.services.ReflectionService
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * Get entity reflection data
 */
val <T : Any> KClass<T>.jsReflect
    get() = ReflectionService.jsReflectOf(this)

/**
 * Get entity reflection data
 */
val KFunction<*>.jsReflect
    get() = ReflectionService.jsReflectOf(this)

/**
 * Get function identifier representation
 */
fun getIdentifier(kFunction: KFunction<*>) = FunctionIdentifier(
        kFunction.name,
        kFunction.toString(),
        kFunction
)

/**
 * Get function identifier representation
 */
fun <T: KFunction<*>> getIdentifierBySupplier(kFunction: T): FunctionIdentifier {
    return FunctionIdentifier(
            kFunction.name,
            kFunction.toString(),
            kFunction
    )
}

/**
 * @return true if function identifier is method identifier
 */
fun FunctionIdentifier.isMethod() = body.matches("^function.*\\(\\\$receiver")

internal external object Object {
    fun create(prototype: dynamic): dynamic
}

internal fun Object.create() = Object.create(null)