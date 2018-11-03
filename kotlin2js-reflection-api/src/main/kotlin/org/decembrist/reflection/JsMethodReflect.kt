package org.decembrist.reflection

/**
 * Method reflection data
 */
interface JsMethodReflect<T>: IAnnotated {

    /**
     * Return method name
     */
    val name: String

    /**
     * Invoke function with arguments
     *
     * @param receiver object
     * @param args arguments
     * @return function result
     */
    operator fun invoke(receiver: T, vararg args: Any): Any

}