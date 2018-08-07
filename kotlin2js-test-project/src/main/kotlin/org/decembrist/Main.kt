package org.decembrist

import org.decembrist.utils.jsReflect
import UnpackagedClass
import UnpackagedAnnotation

private annotation class PrivateAnnotation

@Target(AnnotationTarget.FUNCTION)
annotation class HiderOrderedFunctionAnnotation(val string: String,
                                                val byte: Byte,
                                                val short: Short,
                                                val int: Int,
                                                val long: Long,
                                                val float: Float,
                                                val double: Double,
                                                val char: Char,
                                                val bool: Boolean)

@Target(AnnotationTarget.CLASS)
annotation class ClassAnnotation

@Target(AnnotationTarget.FUNCTION)
annotation class MethodAnnotation

@HiderOrderedFunctionAnnotation(
        "test1",
        1,
        1,
        1,
        1L,
        1.0f,
        1.0,
        't',
        true
)
@UnpackagedAnnotation("test")
@PrivateAnnotation
fun main(args: Array<String>) {
    checkHiderOrderedFunctionAnnotation()
    checkUnpackagedClassAnnotation()
    console.log("OK! Assertions count: $assertionsCount")
}

fun checkHiderOrderedFunctionAnnotation() {
    val annotations = ::main.jsReflect.annotations
    val annotation1 = annotations
            .first { it is HiderOrderedFunctionAnnotation }
            as HiderOrderedFunctionAnnotation
    val annotation2 = annotations
            .first { it is UnpackagedAnnotation } as UnpackagedAnnotation
    assertTrue(annotations.size == 2)
    assertTrue(annotation1.string == "test1")
    assertTrue(annotation1.byte == (1).toByte())
    assertTrue(annotation1.short == (1).toShort())
    assertTrue(annotation1.int == 1)
    assertTrue(annotation1.long == 1L)
    assertTrue(annotation1.float == 1.0f)
    assertTrue(annotation1.double == 1.0)
    assertTrue(annotation1.char == 't')
    assertTrue(annotation1.bool)
    assertTrue(annotation2.name == "test")
}

fun checkUnpackagedClassAnnotation() {
    val annotations = UnpackagedClass::class.jsReflect.annotations
    val classAnnotation = annotations
            .firstOrNull { it is ClassAnnotation }
            ?.let { it as ClassAnnotation }
    val unpackagedAnnotation = annotations
            .firstOrNull { it is UnpackagedAnnotation }
            ?.let { it as UnpackagedAnnotation }
    assertTrue(annotations.size == 2)
    assertTrue(classAnnotation != null)
    val methods = UnpackagedClass::class.jsReflect.methods
    assertTrue(methods.size == 2)
    val method1 = methods.component1()
    val method2 = methods.component2()
    assertTrue(method1.name == "someMethod")
    assertTrue(method2.name == "someMethod")
    val receiver = UnpackagedClass()
    val method1Result = method1.invoke(receiver)
    val method2Result = method2.invoke(receiver, "test2")
    assertTrue(method1Result == "test1")
    assertTrue(method2Result == "test2")
    assertTrue(unpackagedAnnotation != null)
    assertTrue(unpackagedAnnotation!!.name == "unpackaged")
}

var assertionsCount = 0

fun assertTrue(expr: Boolean) {
    console.asDynamic().assert(expr)
    assertionsCount++
}

