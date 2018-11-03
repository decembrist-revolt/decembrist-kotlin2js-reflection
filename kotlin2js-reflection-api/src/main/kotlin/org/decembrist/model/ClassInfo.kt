package org.decembrist.model

import org.decembrist.reflection.IAnnotated
import kotlin.reflect.KClass

/**
 * Class reflection info
 */
class ClassInfo<T : Any>(val clazz: KClass<T>,
                         val methods: List<MethodInfo>,
                         override val annotations: List<Annotation>): IAnnotated