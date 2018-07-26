package org.decembrist.domain.content.classes

import org.decembrist.domain.content.members.Method
import org.decembrist.domain.content.members.Field
import org.decembrist.domain.content.members.IMembered
import org.decembrist.domain.headers.annotations.AnnotationInstance
import org.decembrist.domain.modifiers.ClassModifiers

open class Class(name: String,
                 classModifiers: ClassModifiers)
    : AbstractClass(
        name,
        classModifiers), IMembered {

    constructor(name: String,
                isAbstract: Boolean,
                isFinal: Boolean,
                isOpen: Boolean,
                isData: Boolean,
                isInner: Boolean,
                isSealed: Boolean)
            : this(name, ClassModifiers(
            isAbstract,
            isFinal,
            isOpen,
            isData,
            isInner,
            isSealed))

    override var annotations: Set<AnnotationInstance> = emptySet()

    override val methods: MutableSet<Method> = mutableSetOf()

    override val fields: MutableSet<Field> = mutableSetOf()

}

