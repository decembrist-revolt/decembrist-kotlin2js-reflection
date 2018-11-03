package org.decembrist.reflection

/**
 * Class reflection data
 */
interface JsClassReflect<T>: IAnnotated {

    /**
     * @return class name from compiled js file
     */
    val jsName: String

    /**
     * @return class js constructor function
     */
    val jsConstructor: dynamic

    /**
     * @return class methods reflection data list
     */
    val methods: List<JsMethodReflect<T>>

    /**
     * Create a new class instance through js constructor
     */
    fun createInstance(arguments: Array<Any> = js("[]")): T

}