package org.decembrist.reflection

interface JsClassReflect<T>: IAnnotated {

    /**
     * @return class name from compiled js file
     */
    val jsName: String

    /**
     * @return class js constructor function
     */
    val jsConstructor: dynamic

    val methods: List<JsMethodReflect>

    fun createInstance(arguments: Array<Any> = js("[]")): T

}