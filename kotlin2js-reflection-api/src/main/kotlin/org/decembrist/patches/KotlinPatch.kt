package org.decembrist.patches

import org.decembrist.reflection.Reflection
import org.decembrist.services.ReflectionService.REFLECTION_INFO
import org.decembrist.utils.Object
import org.decembrist.utils.create
import kotlin.reflect.KClass
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction

fun main(args: Array<String>) {
    patchGetKClass()
    patchGetCallableRef()
}

@JsName("getKClassTmp")
private lateinit var getKClassTmp: KFunction1<KClass<*>, KClass<*>>

private fun patchGetKClass() {
    eval("""
        getKClassTmp = Kotlin.getKClass;
        Kotlin.getKClass = getKClass;
    """)
}

@JsName("getKClass")
private fun getKClass(jClass: KClass<*>): KClass<*> {
    val kClass = getKClassTmp.invoke(jClass)
    kClass.asDynamic()[REFLECTION_INFO] = Object.create()
    kClass.asDynamic()[REFLECTION_INFO].jsName = jClass.asDynamic().name
    kClass.asDynamic()[REFLECTION_INFO].jsConstructor = jClass
    kClass.asDynamic()[REFLECTION_INFO].getAnnotations = {
        Reflection.getAnnotations(kClass)
    }
    kClass.asDynamic()[REFLECTION_INFO].createInstance = fun(args: Array<*>): dynamic {
        val obj =  Object.create(jClass.asDynamic().prototype)
        jClass.asDynamic().apply(obj, args)
        return obj
    }
    return kClass
}

private fun patchGetCallableRef() {
    eval("""Kotlin.getCallableRef = getCallableRef;""")
}

@JsName("getCallableRef")
private fun getCallableRef(name: String, kFunction: KFunction<*>): KFunction<*> {
    kFunction.asDynamic().callableName = name
    kFunction.asDynamic()[REFLECTION_INFO] = Object.create()
    kFunction.asDynamic()[REFLECTION_INFO].getAnnotations = { kClass: KClass<*>? ->
        Reflection.getAnnotations(kFunction, kClass)
    }
    return kFunction
}