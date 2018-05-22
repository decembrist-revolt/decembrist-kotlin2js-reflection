package org.decembrist.services

import org.decembrist.reflection.JsClassReflect
import org.decembrist.reflection.JsFunctionReflect
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal object ReflectionService {

    const val REFLECTION_INFO = "reflection-info"

    var isCacheEnabled = true

    private val classCache = mutableMapOf<String, JsClassReflect<*>>()

    fun jsReflectOf(kFunction: KFunction<*>): JsFunctionReflect {
        val getAnnotations = kFunction.asDynamic()[REFLECTION_INFO].getAnnotations
                .unsafeCast<(KClass<*>?) -> List<Annotation>>()
        return object : JsFunctionReflect {
            override val jsName: String by lazy {
                kFunction.toString()
                        .substringAfter("return ")
                        .substringBefore("(")
            }
            override val annotations: List<Annotation>
                get() = getAnnotations(null)
            override fun getAnnotations(kClass: KClass<*>?) = getAnnotations(kClass)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> jsReflectOf(kClass: KClass<T>): JsClassReflect<T> {
        val jsName = kClass.asDynamic()[REFLECTION_INFO].jsName.unsafeCast<String>()
        val getAnnotations = kClass.asDynamic()[REFLECTION_INFO].getAnnotations
                .unsafeCast<() -> List<Annotation>>()
        val cachedValue = if (isCacheEnabled) {
            classCache[jsName]?.let { it as JsClassReflect<T> }
        } else null
        return cachedValue ?: object : JsClassReflect<T> {
            override val jsName: String
                get() = jsName
            override val jsConstructor: dynamic
                get() = kClass.asDynamic()[REFLECTION_INFO].jsConstructor

            override fun createInstance(arguments: Array<Any>): T {
                return kClass.asDynamic()[REFLECTION_INFO]
                        .createInstance(arguments)
                        .unsafeCast<T>()
            }
            override val annotations: List<Annotation>
                get() = getAnnotations()
        }.apply {
            classCache[jsName] = this
        }
    }

    fun clearClassCache() = classCache.clear()

}

