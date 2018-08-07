package org.decembrist.reflection

interface JsMethodReflect<T>: IAnnotated {

    val name: String

    operator fun invoke(receiver: T, vararg args: Any): Any

}