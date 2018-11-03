package org.decembrist.services

import org.decembrist.reflection.JsClassReflect
import org.decembrist.reflection.JsFunctionReflect
import org.decembrist.reflection.JsMethodReflect
import org.decembrist.reflection.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal object ReflectionService {

    const val REFLECTION_INFO = "reflection-info"

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

    fun <T : Any> jsReflectOf(kClass: KClass<T>): JsClassReflect<T> {
        val jsName = kClass.asDynamic()[REFLECTION_INFO].jsName.unsafeCast<String>()
        val getAnnotations = kClass.asDynamic()[REFLECTION_INFO].getAnnotations
                .unsafeCast<() -> List<Annotation>>()
        return object : JsClassReflect<T> {
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

            override val methods: List<JsMethodReflect<T>>
                get() = getMethods(kClass)
        }
    }

    private fun <T: Any> getMethods(kClass: KClass<T>): List<JsMethodReflect<T>> {
        val methodList = Reflection.getMethods(kClass)
                .map {
                    object : JsMethodReflect<T> {
                        override val annotations: List<Annotation>
                            get() = it.annotations
                        override val name: String
                            get() = it.method.name
                        override fun invoke(receiver: T, vararg args: Any): Any {
                            val currentArgs = arrayOf(receiver, *args)
                            return it.method.function.asDynamic()
                                    .apply(null, currentArgs)
                                    .unsafeCast<Any>()
                        }
                    }
                }
        return methodList.unsafeCast<List<JsMethodReflect<T>>>()
    }

}

