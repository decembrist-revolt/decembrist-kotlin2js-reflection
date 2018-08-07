package org.decembrist.domain.content.annotations

import org.decembrist.domain.content.classes.AbstractClass
import org.decembrist.domain.modifiers.ClassModifiers
import kotlin.reflect.KVisibility

class AnnotationClass(name: String,
                      classModifiers: ClassModifiers,
                      visibility: KVisibility): AbstractClass(name, classModifiers, visibility) {

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

    val parameters: MutableList<AnnotationParameter> = mutableListOf()

}