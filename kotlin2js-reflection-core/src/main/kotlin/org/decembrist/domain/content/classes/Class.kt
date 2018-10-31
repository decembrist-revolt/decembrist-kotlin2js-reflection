package org.decembrist.domain.content.classes

import org.decembrist.domain.content.members.Method
import org.decembrist.domain.content.members.Field
import org.decembrist.domain.content.members.IMembered
import org.decembrist.domain.headers.annotations.AnnotationInstance
import org.decembrist.domain.modifiers.ClassModifiers
import kotlin.reflect.KVisibility

open class Class(name: String,
                 classModifiers: ClassModifiers,
                 isibility: KVisibility)
    : AbstractClass(
        name,
        classModifiers,
        isibility), IMembered {

    constructor(name: String,
                isAbstract: Boolean,
                isFinal: Boolean,
                isOpen: Boolean,
                isData: Boolean,
                isInner: Boolean,
                isSealed: Boolean,
                visibility: KVisibility)
            : this(name, ClassModifiers(
            isAbstract,
            isFinal,
            isOpen,
            isData,
            isInner,
            isSealed), visibility)

    override var annotations: Set<AnnotationInstance> = emptySet()

    override val methods: MutableSet<Method> = mutableSetOf()

    override val fields: MutableSet<Field> = mutableSetOf()

}

