package org.decembrist.classes

import org.decembrist.ClassAnnotation
import org.decembrist.assertTrue
import org.decembrist.utils.jsReflect

class EmptyClass

@ClassAnnotation
class AnotherClass

class AnotherSomeClass() {

    var nameValue = "someName"

    constructor(nameValue: String): this() {
        this.nameValue = nameValue
    }

    fun anotherMethod() = AnotherClass()

    fun name() = nameValue

}

fun checkClasses() {
    val annotations = AnotherClass::class.jsReflect.annotations
    val anotherClass = annotations
            .firstOrNull { it is ClassAnnotation }
            ?.let { it as ClassAnnotation }
    assertTrue(anotherClass != null)
    assertTrue(AnotherClass::class.jsReflect.jsName == "AnotherClass")
    val instance = AnotherClass::class.jsReflect.createInstance()
    assertTrue(instance is AnotherClass)
    val emptyClassAnnotations = EmptyClass::class.jsReflect
            .annotations
    assertTrue(emptyClassAnnotations.isEmpty())
    val anotherClassInst = AnotherClass::class.jsReflect.jsConstructor("newName")

}