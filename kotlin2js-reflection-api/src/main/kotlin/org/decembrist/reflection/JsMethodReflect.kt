package org.decembrist.reflection

/**
 * Method reflection data
 */
interface JsMethodReflect<T>: IAnnotated {

    val name: String

    operator fun invoke(receiver: T, vararg args: Any): Any

}