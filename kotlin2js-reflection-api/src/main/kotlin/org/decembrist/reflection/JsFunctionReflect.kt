package org.decembrist.reflection

import kotlin.reflect.KClass

/**
 * Function reflection data
 */
interface JsFunctionReflect: IAnnotated {

    val jsName: String

    /**
     * Get function annotations
     * @param kClass should be provided if this is method
     */
    fun getAnnotations(kClass: KClass<*>? = null): List<Annotation>

}